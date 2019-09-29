package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.sponge.contexts.OnlinePlayer;
import com.google.inject.Injector;
import com.sun.org.glassfish.gmbal.Description;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.types.IActiveSkill;
import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.common.commands.CommandProcessingException;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.sponge.entities.commandblocks.ConsoleSkillExecutor;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static cz.neumimto.rpg.api.logging.Log.info;

@Singleton
@CommandAlias("nadmin|na")
public class SpongeAdminCommands extends BaseCommand {

    @Inject
    private AdminCommandFacade adminCommandFacade;

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private Injector injector;

    @Inject
    private EffectService effectService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private EntityService entityService;

    @Subcommand("effect add")
    @Description("Adds effect, managed by rpg plugin, to the player")
    public void effectAddCommand(Player executor, OnlinePlayer target, IGlobalEffect effect, long duration, @Default("{}") String[] args) {
        String data = String.join("", args);

        IActiveCharacter character = characterService.getCharacter(target.player);

        try {
            adminCommandFacade.commandAddEffectToPlayer(data, effect, duration, character);
        } catch (CommandProcessingException e) {
            executor.sendMessage(Text.of(e.getMessage()));
        }
    }


    @Subcommand("experiences add")
    @Description("Adds N experiences of given source type to a character")
    public void addExperiencesCommand(Player executor, OnlinePlayer target, double amount, @Optional ClassDefinition classDefinition, @Optional String source) {
        ISpongeCharacter character = characterService.getCharacter(target.player);
        try {
            adminCommandFacade.commandAddExperiences(character, amount, classDefinition, source);
        } catch (CommandProcessingException e) {
            executor.sendMessage(Text.of(e.getMessage()));
        }
    }

    @Subcommand("skill")
    public void adminExecuteSkillCommand(Player executor, ISkill skill, @Flags("level") @Default("1") int level) {
        IActiveCharacter character = characterService.getCharacter(executor);
        if (character.isStub()) {
            throw new RuntimeException("Character is required even for an admin.");
        }
        SkillSettings defaultSkillSettings = skill.getSettings();

        Long l = System.nanoTime();

        PlayerSkillContext playerSkillContext = new PlayerSkillContext(null, skill, character);
        playerSkillContext.setLevel(level);
        SkillData skillData = new SkillData(skill.getId());
        skillData.setSkillSettings(defaultSkillSettings);
        playerSkillContext.setSkillData(skillData);
        playerSkillContext.setSkill(skill);

        SkillContext skillContext = new SkillContext((IActiveSkill) skill, playerSkillContext) {{
            wrappers.add(new SkillExecutorCallback() {
                @Override
                public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                    Long e = System.nanoTime();
                    character.sendMessage("Exec Time: " + TimeUnit.MILLISECONDS.convert(e - l, TimeUnit.NANOSECONDS));
                    if (character instanceof ConsoleSkillExecutor) {
                        Living entity = (Living) character.getEntity();
                        entity.remove();
                    }
                }
            });
        }};
        skillContext.sort();
        skillContext.next(character, playerSkillContext, skillContext);
    }

    @Subcommand("inspect-property")
    public void inspectPropertyCommand(Player executor, OnlinePlayer target, String property) {
        try {
            int idByName = propertyService.getIdByName(property);
            IActiveCharacter character = characterService.getCharacter(target.player);
            executor.sendMessage(Text.of(TextColors.GOLD, "=================="));
            executor.sendMessage(Text.of(TextColors.GREEN, property));

            executor.sendMessage(Text.of(TextColors.GOLD, "Value", TextColors.WHITE, "/",
                    TextColors.AQUA, "Effective Value", TextColors.WHITE, "/",
                    TextColors.GRAY, "Cap",
                    TextColors.DARK_GRAY, " .##"));

            NumberFormat formatter = new DecimalFormat("#0.00");
            executor.sendMessage(Text.of(TextColors.GOLD, formatter.format(character.getProperty(idByName)), TextColors.WHITE, "/",
                    TextColors.AQUA, formatter.format(entityService.getEntityProperty(character, idByName)), TextColors.WHITE, "/",
                    TextColors.GRAY, formatter.format(propertyService.getMaxPropertyValue(idByName))));

            executor.sendMessage(Text.of(TextColors.GOLD, "=================="));
            executor.sendMessage(Text.of(TextColors.GRAY, "Memory/1 player: " + (character.getPrimaryProperties().length * 2 * 4) / 1024.0 + "kb"));

        } catch (Throwable t) {
            executor.sendMessage(Text.of("No such property"));
        }
    }

    @Subcommand("reload")
    public void reload(Player executor) {
        info("[RELOAD] Reading Settings.conf file: ");
        Rpg.get().reloadMainPluginConfig();
        info("[RELOAD] Reading Entity conf files: ");
        Rpg.get().getEntityService().reload();

        info("[RELOAD] Scripts ");
        IScriptEngine jsLoader = injector.getInstance(IScriptEngine.class);
        jsLoader.initEngine();
        jsLoader.reloadSkills();

        ClassDefinitionDao build = injector.getInstance(ClassDefinitionDao.class);
        try {
            info("[RELOAD] Checking class files: ");

            Set<ClassDefinition> classDefinitions = build.parseClassFiles();
            info("[RELOAD] Class files ok");

            info("[RELOAD] Saving current state of players");
            Set<CharacterBase> characterBases = new HashSet<>();
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                IActiveCharacter character = characterService.getCharacter(player);
                if (character.isStub()) {
                    continue;
                }
                characterBases.add(character.getCharacterBase());
            }

            Log.info("[RELOAD] Purging effect caches");
            effectService.purgeEffectCache();
            effectService.stopEffectScheduler();

            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                IActiveCharacter character = characterService.getCharacter(player);
                if (character.isStub()) {
                    continue;
                }
                ISpongeCharacter preloadCharacter = characterService.buildDummyChar(player.getUniqueId());
                characterService.registerDummyChar(preloadCharacter);
            }

            for (CharacterBase characterBase : characterBases) {
                Log.info("[RELOAD] saving character " + characterBase.getLastKnownPlayerName());
                characterService.save(characterBase);
            }

            System.gc();

            effectService.startEffectScheduler();
            Rpg.get().getClassService().loadClasses();

            Comparator<CharacterBase> cmp = Comparator.comparing(CharacterBase::getUpdated);

            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                List<CharacterBase> playersCharacters = characterService.getPlayersCharacters(player.getUniqueId());
                if (playersCharacters.isEmpty()) {
                    continue;
                }
                CharacterBase max = playersCharacters.stream().max(cmp).get();
                ISpongeCharacter activeCharacter = characterService.createActiveCharacter(player.getUniqueId(), max);
                characterService.setActiveCharacter(player.getUniqueId(), activeCharacter);
                characterService.invalidateCaches(activeCharacter);
                characterService.assignPlayerToCharacter(player.getUniqueId());
            }
        } catch (ObjectMappingException e) {

        }
    }
}