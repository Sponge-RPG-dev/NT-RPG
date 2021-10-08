package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.effects.model.EffectModelFactory;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.events.skill.SkillPostUsageEvent;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.scripting.IRpgScriptEngine;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.utils.ActionResult;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.utils.GraalInstaller;
import cz.neumimto.rpg.common.utils.model.InstallOptions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cz.neumimto.rpg.common.logging.Log.info;

@Singleton
@CommandAlias("nadmin|na")
@CommandPermission("ntrpg.admin")
public class AdminCommands extends BaseCommand {

    @Inject
    private EffectService effectService;

    @Inject
    private CharacterService characterService;

    @Inject
    private InfoCommands infoCommands;

    @Inject
    private IRpgScriptEngine scriptEngine;

    @Inject
    private AssetService assetService;

    private Gson gson = new Gson();

    @CommandCompletion("@players @class-any")
    @Subcommand("set-class")
    public void hardResetPlayer(CommandIssuer commandIssuer, OnlineOtherPlayer player, ClassDefinition classDefinition) {
        PlayerClassData classByType = player.character.getClassByType(classDefinition.getClassType());
        if (classByType != null) {
            characterService.removeClassFromSlot(player.character, classDefinition.getClassType());
        }
        characterService.addNewClass(player.character, classDefinition);
    }

    @CommandCompletion("@players")
    @Subcommand("attributepoints add")
    @Description("Permanently adds X skillpoints to a player")
    public void addAttributePoints(CommandIssuer commandSender, OnlineOtherPlayer player, @Default("1") int amount) {
        characterService.characterAddAttributePoints(player.character, amount);
    }


    @CommandCompletion("@players @class-any")
    @Subcommand("skillpoints add")
    @Description("Permanently adds X skillpoints to a player")
    public void addSkillPointsCommand(CommandIssuer commandSender, OnlineOtherPlayer player, ClassDefinition characterClass, @Default("1") int amount) {
        IActiveCharacter character = player.character;
        PlayerClassData classByName = character.getClassByName(characterClass.getName());
        if (classByName == null) {
            throw new IllegalArgumentException("Player " + character.getPlayerAccountName() + " character " + character.getName() + " do not have class " + characterClass.getName());
        }
        characterService.characterAddSkillPoints(character, characterClass, amount);

    }

    @CommandCompletion("@players @geffect")
    @Subcommand("effect add")
    @Description("Adds effect, managed by rpg plugin, to the player")
    public void effectAddCommand(CommandIssuer commandSender, OnlineOtherPlayer player, IGlobalEffect effect, long duration, String[] args) {
        String data = String.join("", args);
        IActiveCharacter character = player.character;
        try {
            commandAddEffectToPlayer(data, effect, duration, character);
        } catch (CommandProcessingException e) {
            commandSender.sendMessage(e.getMessage());
        }
    }

    @CommandCompletion("@players @nothing @nothing")
    @Subcommand("exp")
    @Description("Adds N experiences of given source type to a character")
    public void addExperiencesCommand(CommandIssuer executor, OnlineOtherPlayer target, double amount, String classOrSource) {
        try {
            commandAddExperiences(target.character, amount, classOrSource);
        } catch (CommandProcessingException e) {
            executor.sendMessage(e.getMessage());
        }
    }

    @Subcommand("skill")
    @CommandCompletion("@skilltree @nothing @skillskctx")
    public void adminExecuteSkillCommand(IActiveCharacter character, SkillTree tree, int level, ISkill skill) {
        long e = System.nanoTime();
        commandExecuteSkill(character, tree, skill, level);
        if (Rpg.get().getPluginConfig().DEBUG.isBalance()) {
            character.sendMessage("Exec Time: " + TimeUnit.MILLISECONDS.convert(e - System.currentTimeMillis(), TimeUnit.NANOSECONDS));
        }
    }

    @Subcommand("cast-skill-as")
    @CommandCompletion("@skilltree @nothing @skillskctx")
    public void adminExecuteSkillCommandAs(OnlineOtherPlayer executor, SkillTree tree, int level, ISkill skill) {
        commandExecuteSkill(executor.character, tree, skill, level);
    }

    @CommandCompletion("@players @classtypes")
    @Subcommand("classes")
    public void showClassesCommandAdmin(CommandIssuer console, OnlineOtherPlayer executor, @Optional String type) {
        infoCommands.showClassesCommand(executor.character, type);
    }

    @Subcommand("class")
    @CommandCompletion("@players @class-any")
    public void showClassCommandAdmin(CommandIssuer console, OnlineOtherPlayer executor, ClassDefinition classDefinition, @Optional String back) {
        infoCommands.showClassCommand(executor.character, classDefinition, back);
    }

    @CommandCompletion("@players @class-any")
    @Subcommand("add-class")
    public void addClassToCharacterCommand(CommandIssuer executor, OnlineOtherPlayer target, ClassDefinition klass) {
        ActionResult actionResult = addCharacterClass(target.character, klass);
        if (actionResult.isOk()) {
            executor.sendMessage(Rpg.get().getLocalizationService().translate("class.set.ok"));
        } else {
            executor.sendMessage(actionResult.getMessage());
        }
    }

    @CommandCompletion("@players @classtypes @nothing")
    @Subcommand("add-unique-skillpoint")
    public void addUniqueSkillpoint(CommandIssuer executor, OnlineOtherPlayer target, String classType, String sourceKey) {
        IActiveCharacter character = target.character;
        if (character.isStub()) {
            throw new IllegalStateException("Stub character");
        }
        commandAddUniqueSkillpoint(character, classType, sourceKey);
    }

    @Description("Copies default configurations from within the ntrpg.jar to filesystem for editing")
    @Subcommand("install")
    public void install(CommandIssuer executor, InstallOptions installOption) {
        Path workingDir = Paths.get(Rpg.get().getWorkingDirectory());
        if (installOption.installClasses()) {
            assetService.copyDefaultClasses(workingDir);
            executor.sendMessage("Internal assets were copied to ntrpg/classes ntrpg/skilltrees folder." +
                    " You can now edit files and reload /ntrpg reload for immediate change");
        }
        if (installOption.installGuis()) {
            assetService.copyDefaultGuis(workingDir);
            executor.sendMessage("Internal assets were copied to ntrpg/guis folder." +
                    " You can now edit files and reload /ntrpg reload for immediate change");
        }

        if (installOption.installLocalizations()) {
            assetService.copyDefaultLocalizations(workingDir);
            executor.sendMessage("Internal assets were copied to ntrpg/localizations folder." +
                    " You can now edit files and reload /ntrpg reload for immediate change");
        }
    }

    //https://medium.com/graalvm/graalvms-javascript-engine-on-jdk11-with-high-performance-3e79f968a819
    @Subcommand("graal?")
    public void checkGraalPresent(CommandIssuer executor) {
        if (GraalInstaller.check() == null) {
            executor.sendMessage("To enable js scripting you additional action is required.");
            executor.sendMessage(">1) Run your server on GraalVM https://www.graalvm.org/downloads/, instead of standard JRE");
            executor.sendMessage(">2) Run command /nadmin install-graal ; this action will download several Graal binaries from https://mvnrepository.com/artifact/org.graalvm/ !");
            executor.sendMessage("> ! If you choose the second option you will also want to add additional server startup flags -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI !");
        } else {
            executor.sendMessage("- OK");
        }
    }

    @Private
    @Subcommand("install-graal")
    public void installGraal(CommandIssuer commandIssuer) {
        GraalInstaller.downloadTo(Paths.get(Rpg.get().getWorkingDirectory() + "/addons"), commandIssuer::sendMessage);
    }

    @Subcommand("reload")
    public void reload(@Optional @Default("a") String arg) {
        boolean reloadAll = arg.equalsIgnoreCase("a");
        boolean reloadJs = reloadAll || arg.equalsIgnoreCase("js");
        boolean reloadLocalizations = reloadAll || arg.equalsIgnoreCase("l");
        boolean reloadItems = reloadAll || arg.equalsIgnoreCase("i");
        boolean reloadSkills = reloadAll || arg.equalsIgnoreCase("s");
        boolean reloadGuis = reloadAll || arg.equalsIgnoreCase("g");
        boolean reloadClasses = reloadAll || reloadItems || reloadSkills || arg.equalsIgnoreCase("c");

        info("[RELOAD] Saving current state of players");
        Set<CharacterBase> characterBases = new HashSet<>();
        for (UUID uuid : getAllOnlinePlayers()) {
            IActiveCharacter character = characterService.getCharacter(uuid);
            if (character.isStub()) {
                continue;
            }
            characterBases.add(character.getCharacterBase());
        }
        for (CharacterBase characterBase : characterBases) {
            Log.info("[RELOAD] saving character " + characterBase.getLastKnownPlayerName());
            characterService.save(characterBase);
        }

        for (UUID uuid : getAllOnlinePlayers()) {
            IActiveCharacter character = characterService.getCharacter(uuid);
            if (character.isStub()) {
                continue;
            }
            IActiveCharacter preloadCharacter = characterService.buildDummyChar(uuid);
            characterService.registerDummyChar(preloadCharacter);
        }

        if (reloadAll) {
            info("[RELOAD] Reading Settings.conf file: ");
            Rpg.get().reloadMainPluginConfig();
        }

        if (reloadLocalizations) {
            info("[RELOAD] Reading localization files: ");
            Locale locale = Locale.forLanguageTag(Rpg.get().getPluginConfig().LOCALE);
            try {
                Rpg.get().getResourceLoader().reloadLocalizations(locale);
            } catch (Exception e) {
                Log.error("Could not read localizations in locale " + locale.toString() + " - " + e.getMessage());
            }
        }

        if (reloadAll) {
            info("[RELOAD] Reading Entity conf files: ");
            Rpg.get().getEntityService().reload();
        }

        if (reloadJs) {
            info("[RELOAD] Scripts ");
            scriptEngine.prepareEngine();
        }

        if (reloadItems) {
            info("[RELOAD] ItemGroups ");
            Rpg.get().getItemService().reload();
            Rpg.get().getInventoryService().reload();
        }

        if (reloadSkills) {
            info("[RELOAD] Properties, Attributes, Skills");
            Rpg.get().getSkillService().load();
            Rpg.get().getPropertyService().reload();
        }

        if (reloadClasses) {
            info("[RELOAD] Classes");
            Rpg.get().getExperienceService().reload();

            try {
                info("[RELOAD] Checking class files: ");
                Rpg.get().getClassService().load();
                info("[RELOAD] Class files ok");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.info("[RELOAD] Purging effect caches");
        effectService.purgeEffectCache();
        effectService.stopEffectScheduler();

        System.gc();

        effectService.startEffectScheduler();

        for (UUID uuid : getAllOnlinePlayers()) {
            List<CharacterBase> playersCharacters = characterService.getPlayersCharacters(uuid);
            if (playersCharacters.isEmpty()) {
                continue;
            }
            CharacterBase max = playersCharacters.stream().max(Comparator.comparing(CharacterBase::getUpdated)).get();
            IActiveCharacter activeCharacter = characterService.createActiveCharacter(uuid, max);
            characterService.setActiveCharacter(uuid, activeCharacter);
            characterService.assignPlayerToCharacter(uuid);
        }

        doImplSpecificReload();
    }

    public boolean commandAddEffectToPlayer(String data, IGlobalEffect effect, long duration, IActiveCharacter character) throws CommandProcessingException {
        EffectParams map = new EffectParams();
        Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
        if (data == null) {
            if (modelType != Void.TYPE)
                throw new CommandProcessingException("Effect data expected! Use ? as data to list parameters");
        } else {
            if (data.equals("?")) {
                if (modelType == Void.TYPE) {
                    Log.error("No data expected");
                    return false;
                } else if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
                    Log.error("Expected: " + modelType.getTypeName());
                    return false;
                } else {
                    Map<String, String> q = new HashMap<>();
                    for (Field field : modelType.getFields()) {
                        q.put(field.getName(), field.getType().getName());
                    }
                    Log.error("Expected: " + gson.toJson(q));
                    return false;
                }
            }
            if (modelType == Void.TYPE) {
                //Just do nothing
            } else if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
                map.put("value", data);
            } else try {
                //Get rid of unused entries in data string and check for missing
                EffectParams tempMap = gson.fromJson(data, EffectParams.class);
                for (Field field : modelType.getFields()) {
                    if (Modifier.isTransient(field.getModifiers())) continue;
                    if (!tempMap.containsKey(field.getName())) {
                        throw new CommandProcessingException("Missing parameter: " + field.getName());
                    }
                    map.put(field.getName(), tempMap.get(field.getName()));
                }
            } catch (JsonSyntaxException e) {
                Map<String, String> q = new HashMap<>();
                for (Field field : modelType.getFields()) {
                    q.put(field.getName(), field.getType().getName());
                }
                throw new CommandProcessingException("Expected: " + gson.toJson(q));
            }
        }

        if (effectService.addEffect(effect.construct(character, duration, map), InternalEffectSourceProvider.INSTANCE)) {
            Log.info("Effect " + effect.getName() + " applied to player " + character.getUUID());
            return true;
        }
        return false;
    }

    public boolean commandAddExperiences(IActiveCharacter character, Double amount, String classOrSource) throws CommandProcessingException {
        Collection<PlayerClassData> classes = character.getClasses().values();

        ClassDefinition classDefinition = Rpg.get().getClassService().getClassDefinitionByName(classOrSource);
        String expSource = classOrSource.toUpperCase();

        if (classDefinition != null) {
            classes.stream()
                    .filter(PlayerClassData::takesExp)
                    .filter(c -> c.getClassDefinition().getName().equalsIgnoreCase(classDefinition.getName()))
                    .forEach(c -> characterService.addExperiences(character, amount, c));
        } else {
            characterService.addExperiences(character, amount, expSource.toUpperCase());
        }
        return true;
    }

    public ActionResult addCharacterClass(IActiveCharacter c, ClassDefinition classDefinition) {
        ActionResult actionResult = characterService.canGainClass(c, classDefinition);
        if (actionResult.isOk()) {
            characterService.addNewClass(c, classDefinition);
        }
        return actionResult;
    }

    public void commandExecuteSkill(IActiveCharacter character, SkillTree skillTree, ISkill skill, int level) {
        if (character.isStub()) {
            throw new RuntimeException("Character is required even for an admin.");
        }
        Long l = System.nanoTime();

        PlayerSkillContext playerSkillContext = new PlayerSkillContext(null, skill, character);
        playerSkillContext.setLevel(level);
        SkillData skillData = skillTree.getSkillById(skill.getId());
        if (skillData == null) {
            throw new IllegalArgumentException("Skill " + skillData.getSkillId() + " is not present in the tree " + skillTree.getId());
        }

        playerSkillContext.setSkillData(skillData);
        playerSkillContext.setSkill(skill);

        SkillResult result = Rpg.get().getSkillService().executeSkill(character, playerSkillContext);

        if (result == SkillResult.OK) {
            SkillPostUsageEvent eventPost = Rpg.get().getEventFactory().createEventInstance(SkillPostUsageEvent.class);
            eventPost.setSkill(skill);
            eventPost.setCaster(character);
            Rpg.get().postEvent(eventPost);
        }
    }

    public void commandAddUniqueSkillpoint(IActiveCharacter character, String classType, String sourceKey) {
        PlayerClassData classByType = character.getClassByType(classType);
        if (classByType != null) {
            ActionResult result = characterService.addUniqueSkillpoint(character, classByType, sourceKey);
            if (result.isOk()) {
                characterService.putInSaveQueue(character.getCharacterBase());
            } else {
                Log.warn("Character " + character.getUUID() + " could not gain unique sp ClassType: " + classType + " " + result.getMessage());
            }
        }
    }

    public Set<UUID> getAllOnlinePlayers() {
        return Rpg.get().getOnlinePlayers();
    }

    public void doImplSpecificReload() {
        Rpg.get().doImplSpecificreload();
    }

}
