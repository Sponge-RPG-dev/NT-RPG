package cz.neumimto.rpg.sponge.commands.admin;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.gui.ArmorAndWeaponMenuHelper;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

import static cz.neumimto.rpg.api.logging.Log.info;
import static cz.neumimto.rpg.api.logging.Log.warn;

@Singleton
public class ReloadExecutor implements CommandExecutor {
    
    @Inject
    private IScriptEngine jsLoader;

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private SkillService skillService;

    @Inject
    private EntityService entityService;

    @Inject
    private IEffectService effectService;

    @Inject
    private ClassService classService;

    @Inject
    private SpongeRpgPlugin plugin;


    @Inject
    private Injector injector;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String[] a = args.<String>getOne("args").get().split(" ");
        ArmorAndWeaponMenuHelper.resetAll();
        if (a[0].equalsIgnoreCase("js")) {
            jsLoader.initEngine();

            int i = 1;
            String q = null;
            while (i < a.length) {
                q = a[i];
                if (q.equalsIgnoreCase("skills") || q.equalsIgnoreCase("s")) {
                    jsLoader.reloadSkills();
                    characterService.getCharacters()
                            .stream()
                            .forEach(qw -> {
                                Map<String, PlayerSkillContext> skills = qw.getSkills();
                                for (Map.Entry<String, PlayerSkillContext> entry : skills.entrySet()) {
                                    if (entry.getValue() == PlayerSkillContext.EMPTY) {
                                        continue;
                                    }
                                    PlayerSkillContext value = entry.getValue();
                                    Optional<ISkill> byId = skillService.getById(value.getSkill().getId());
                                    if (!byId.isPresent()) {
                                        throw new RuntimeException("Unabled to reload the skill " + value.getSkill().getId() + ". "
                                                + "Restart the server");
                                    }
                                    ISkill skill = byId.get();
                                    value.setSkill(skill);
                                    value.getSkillData().setSkill(skill);
                                }
                            });
                }
                if (q.equalsIgnoreCase("attributes") || q.equalsIgnoreCase("a")) {
                    jsLoader.reloadAttributes();
                }
                if (q.equalsIgnoreCase("globaleffects") || q.equalsIgnoreCase("g")) {
                    jsLoader.reloadGlobalEffects();
                }
                i++;
            }
        } else if (a[0].equalsIgnoreCase("skilltree")) {
            skillService.reloadSkillTrees();
        } else if (a[0].equalsIgnoreCase("settings")) {
            Rpg.get().reloadMainPluginConfig();
        } else if (a[0].equalsIgnoreCase("mobs")) {
            entityService.reload();
        } else if (a[0].equalsIgnoreCase("classes")) {
            //Check if configs are ok
            warn("[RELOAD] Attempting to reload classes from config files...");
            info("[RELOAD] Checking class files: ");
            ClassDefinitionDao build = injector.getInstance(ClassDefinitionDao.class);
            try {
                build.parseClassFiles();
                info("[RELOAD] Class files ok");

                //Get all objects we need to save
                Set<CharacterBase> characterBases = new HashSet<>();
                for (Player player : Sponge.getServer().getOnlinePlayers()) {
                    IActiveCharacter character = characterService.getCharacter(player);
                    if (character.isStub()) {
                        continue;
                    }
                    characterBases.add(character.getCharacterBase());
                }

                //Set Char stubs
                for (Player player : Sponge.getServer().getOnlinePlayers()) {
                    IActiveCharacter character = characterService.getCharacter(player);
                    if (character.isStub()) {
                        continue;
                    }
                    ISpongeCharacter preloadCharacter = characterService.buildDummyChar(player.getUniqueId());
                    characterService.registerDummyChar(preloadCharacter);
                }
                Log.info("[RELOAD] Purging effect caches");
                effectService.purgeEffectCache();
                effectService.stopEffectScheduler();
                //todo purge all skill
                //todo purge all Skilltrees

                for (CharacterBase characterBase : characterBases) {
                    Log.info("[RELOAD] saving character " + characterBase.getLastKnownPlayerName());
                    characterService.save(characterBase);
                }


                //we should be ready to startEffectScheduler loading stuff back
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    //System.gc(); - for reloading skill its required

                    effectService.startEffectScheduler();
                    //todo load skill
                    //todo load Skilltrees

                    classService.loadClasses();
                    Comparator<CharacterBase> cmp = Comparator.comparing(CharacterBase::getUpdated);
                    for (Player player : Sponge.getServer().getOnlinePlayers()) {
                        List<CharacterBase> playersCharacters =
                                characterService.getPlayersCharacters(player.getUniqueId());
                        if (playersCharacters.isEmpty()) {
                            continue;
                        }
                        CharacterBase max = playersCharacters.stream().max(cmp).get();
                        ISpongeCharacter activeCharacter = characterService.createActiveCharacter(player.getUniqueId(), max);
                        characterService.setActiveCharacter(player.getUniqueId(), activeCharacter);
                        characterService.invalidateCaches(activeCharacter);
                        characterService.assignPlayerToCharacter(player.getUniqueId());
                    }
                }).submit(Rpg.get());
            } catch (ObjectMappingException e) {
                src.sendMessage(Text.of("Errors occured during class reload, check server console for more informations"));
            }
        } else {
            src.sendMessage(TextHelper.parse("js[s/a/g] skilltree [r,a] classes"));
            return CommandResult.empty();
        }
        return CommandResult.success();
    }
}
