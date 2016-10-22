/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.skills;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.events.skills.SkillPostUsageEvent;
import cz.neumimto.rpg.events.skills.SkillPrepareEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.persistance.SkillTreeDao;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by NeumimTo on 1.1.2015.
 */
@Singleton
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
        if (ISkill.getName() == null) {
            String simpleName = ISkill.getClass().getSimpleName();
            if (simpleName.startsWith("Skill")) {
                simpleName = simpleName.substring(5, simpleName.length());
            }
            ISkill.setName(simpleName);
        }
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

    public SkillResult executeSkill(IActiveCharacter character, ISkill skill) {
        if (character.hasSkill(skill.getName())) {
            return executeSkill(character, character.getSkillInfo(skill));
        }
        return SkillResult.WRONG_DATA;
    }

    public SkillResult executeSkill(IActiveCharacter character, ExtendedSkillInfo esi) {
        int level = esi.getLevel();
        if (level < 0)
            //this should never happen
            return SkillResult.WRONG_DATA;
        level += character.getCharacterProperty(DefaultProperties.all_skills_bonus);
        Long aLong = character.getCooldowns().get(esi.getSkill().getName());
        long servertime = System.currentTimeMillis();
        if (aLong != null && aLong > servertime) {
            Gui.sendCooldownMessage(character, esi.getSkill().getName(), ((aLong - servertime) / 1000.0));
            return SkillResult.ON_COOLDOWN;
        }
        SkillData skillData = esi.getSkillData();
        SkillSettings skillSettings = skillData.getSkillSettings();
        float requiredMana = skillSettings.getLevelNodeValue(ISkillNode.MANACOST, level);
        float requiredHp = skillSettings.getLevelNodeValue(ISkillNode.HPCOST, level);
        SkillPrepareEvent event = new SkillPrepareEvent(character, requiredHp, requiredMana);
        game.getEventManager().post(event);
        if (event.isCancelled())
            return SkillResult.FAIL;
        double hpcost = event.getRequiredHp() * character.getCharacterProperty(DefaultProperties.health_cost_reduce);
        double manacost = event.getRequiredMana() * character.getCharacterProperty(DefaultProperties.mana_cost_reduce);
        //todo float staminacost =
        if (character.getHealth().getValue() > hpcost) {
            if (character.getMana().getValue() >= manacost) {
                SkillResult result = esi.getSkill().onPreUse(character);
                if (result == SkillResult.CANCELLED)
                    return SkillResult.CANCELLED;
                if (result == SkillResult.OK) {
                    float newCd = skillSettings.getLevelNodeValue(ISkillNode.COOLDOWN, level);
                    SkillPostUsageEvent eventt = new SkillPostUsageEvent(character, hpcost, manacost, newCd);
                    game.getEventManager().post(eventt);
                    if (!event.isCancelled()) {
                        double newval = character.getHealth().getValue() - eventt.getHpcost();
                        if (newval <= 0) {
                            //todo kill the player ?
                            HealthData healthData = character.getPlayer().getHealthData();
                        } else {
                            character.getHealth().setValue(newval);
                            newCd = eventt.getCooldown() * character.getCharacterProperty(DefaultProperties.cooldown_reduce);
                            character.getMana().setValue(character.getMana().getValue() - event.getRequiredMana());
                            long cd = (long) newCd;
                            character.getCooldowns().put(esi.getSkill().getName(), cd + servertime);
                            Gui.sendManaStatus(character, character.getMana().getValue(), character.getMaxMana(), character.getMana().getReservedAmount());
                            return SkillResult.OK;
                        }
                    }
                }
            }
            return SkillResult.NO_MANA;
        }
        return SkillResult.NO_HP;
    }


    @PostProcess(priority = 300)
    public void load() {
        skillTrees.putAll(skillTreeDao.getAll());
        createSkillsDefaults();

    }

    public void initIcons() {
        Properties properties = new Properties();
        File f = new File(NtRpgPlugin.workingDir, "Icons.properties");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileInputStream stream = new FileInputStream(f)) {
            properties.load(stream);
            for (Map.Entry<Object, Object> l : properties.entrySet()) {
                String skillname = (String) l.getKey();
                String url = (String) l.getValue();

                getSkill(skillname).setIconURL(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder();

        try (FileWriter fileWriter = new FileWriter(path.toFile());
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (ISkill skill : skills.values()) {
                builder.append(skill.getName()).append(": { ").append(Utils.LineSeparator);
                for (Map.Entry<String, Float> entry : skill.getDefaultSkillSettings().getNodes().entrySet()) {
                    builder.append(Utils.Tab).append(entry.getKey()).append(" : ").append(entry.getValue()).append(Utils.LineSeparator);
                }
                builder.append(Utils.Tab).append(" },").append(Utils.LineSeparator);
                bufferedWriter.write(builder.toString());
            }
            bufferedWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
