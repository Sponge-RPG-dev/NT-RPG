package cz.neumimto.skills;

import cz.neumimto.GroupService;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.events.SkillPostUsageEvent;
import cz.neumimto.events.SkillPrepareEvent;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.persistance.SkillTreeDao;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.DefaultProperties;
import cz.neumimto.scripting.JSLoader;
import cz.neumimto.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;

import javax.annotation.concurrent.ThreadSafe;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by NeumimTo on 1.1.2015.
 */
@Singleton
@ThreadSafe
public class SkillService {

    @Inject
    private SkillTreeDao skillTreeDao;

    @Inject
    private GroupService groupService;

    @Inject
    private JSLoader jsLoader;

    @Inject
    private Game game;

    private Map<String, ISkill> skills = new ConcurrentHashMap<>();

    private Map<String, SkillTree> skillTrees = new ConcurrentHashMap<>();

    public void addSkill(ISkill ISkill) {
        if (!PluginConfig.DEBUG) {
            if (skills.containsKey(ISkill.getName().toLowerCase()))
                throw new RuntimeException("Skill " + ISkill.getName() + " already exists");
        }
        skills.put(ISkill.getName().toLowerCase(), ISkill);
    }


    public ISkill getSkill(String name) {
        return skills.get(name.toLowerCase());
    }

    public Map<String, ISkill> getSkills() {
        return skills;
    }


    public Map<String, SkillTree> getSkillTrees() {
        return skillTrees;
    }

    public int executeSkill(IActiveCharacter character,ExtendedSkillInfo esi) {
        int level = esi.getLevel();
        if (level < 0)
            return 1;
        if (character.hasCooldown(esi.getSkill().getName()))
            return 2;
        SkillInfo skillInfo = esi.getSkillInfo();
        SkillSettings skillSettings = skillInfo.getSkillSettings();
        float requiredMana = skillSettings.getLevelNodeValue(SkillNode.MANACOST,level);
        float requiredHp = skillSettings.getLevelNodeValue(SkillNode.HPCOST,level);
        SkillPrepareEvent event = new SkillPrepareEvent(character, requiredHp, requiredMana);
        game.getEventManager().post(event);
        if (event.isCancelled())
            return 3;
        double hpcost = event.getRequiredHp() * character.getCharacterProperty(DefaultProperties.health_cost_reduce);
        double manacost = event.getRequiredMana()*character.getCharacterProperty(DefaultProperties.mana_cost_reduce);
        //todo float staminacost =
        if (character.getHealth().getValue() < hpcost) {
            if (character.getMana().getValue() < (manacost)) {
                long cooldown = (long) (System.currentTimeMillis()+(skillSettings.getLevelNodeValue(SkillNode.COOLDOWN,level)*character.getCharacterProperty(DefaultProperties.cooldown_reduce)));
                SkillResult result = esi.getSkill().onPreUse(character);
                if (result == SkillResult.CANCELLED)
                    return 6;
                if (result == SkillResult.OK) {
                    SkillPostUsageEvent eventt = new SkillPostUsageEvent(character,hpcost,manacost,cooldown);
                    game.getEventManager().post(eventt);
                    if (!event.isCancelled()) {
                        double newval = character.getHealth().getValue() - eventt.getHpcost();
                        if (newval <= 0) {
                            //todo kill the player
                            HealthData healthData = character.getPlayer().getHealthData();
                        } else {
                            character.getHealth().setValue(newval);
                            character.getMana().setValue(character.getMana().getValue() - event.getRequiredMana());
                            if (cooldown <= System.currentTimeMillis()) {
                                character.getCharacterBase().getCooldowns().put(skillInfo.getSkillName(), cooldown);
                            }
                            return 0;
                        }
                    }
                }
            }
            return 4;
        }
        return 5;
    }


    @PostProcess(priority = 300)
    public void load() {
        skillTrees.putAll(skillTreeDao.getAll());
        createSkillsDefaults();
    }

    public void deleteConfFile() {
        Path path = Paths.get(NtRpgPlugin.workingDir + "/skills-nodelist.conf");
        if (Files.exists(path))
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void createSkillsDefaults() {
        Path path = Paths.get(NtRpgPlugin.workingDir + "/skills-nodelist.conf");
        if (!Files.exists(path)) {
            try {
                Path file = Files.createFile(path);
                StringBuilder builder = new StringBuilder();
                FileWriter fileWritter = new FileWriter(path.toFile(), true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                for (ISkill skill : skills.values()) {
                    builder.append(skill.getName()).append(": { ").append(Utils.LineSeparator);
                    for (Map.Entry<String, Float> entry : skill.getDefaultSkillSettings().getNodes().entrySet()) {
                        builder.append(Utils.Tab).append(entry.getKey()).append(" : ").append(entry.getValue()).append(Utils.LineSeparator);
                    }
                    builder.append(" }");
                    bufferWritter.write(builder.toString());
                    bufferWritter.flush();
                }
                bufferWritter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
