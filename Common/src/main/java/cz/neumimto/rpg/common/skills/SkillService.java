package cz.neumimto.rpg.common.skills;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.google.inject.Injector;
import cz.neumimto.nts.NTScript;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.configuration.SkillDumpConfiguration;
import cz.neumimto.rpg.common.configuration.SkillDumpConfigurations;
import cz.neumimto.rpg.common.configuration.SkillTreeLoaderImpl;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.scripting.NTScriptEngine;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.scripting.ActiveScriptSkill;
import cz.neumimto.rpg.common.skills.scripting.EffectScriptGenerator;
import cz.neumimto.rpg.common.skills.scripting.ListenerScriptGenerator;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.common.skills.types.PassiveScriptSkill;
import cz.neumimto.rpg.common.skills.types.ScriptSkill;
import cz.neumimto.rpg.common.utils.ClassUtils;
import cz.neumimto.rpg.common.utils.DebugLevel;
import cz.neumimto.rpg.common.utils.annotations.CatalogId;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static cz.neumimto.rpg.common.logging.Log.*;

public abstract class SkillService {

    protected Map<String, ISkill> skills = new HashMap<>();
    protected Map<String, SkillTree> skillTrees = new ConcurrentHashMap<>();
    protected Map<String, ISkill> skillByNames = new HashMap<>();
    protected Map<String, ISkillType> skillTypes = new HashMap<>();

    @Inject
    protected SkillTreeLoaderImpl skillTreeLoader;

    @Inject
    protected ClassService classService;

    @Inject
    protected Injector injector;

    @Inject
    protected AssetService assetService;

    @Inject
    protected NTScriptEngine ntScriptEngine;

    private Map<String, SkillScriptHandlers> skillHandlers = new HashMap<>();

    public SkillService() {
        Stream.of(SkillType.values()).forEach(this::registerSkillType);
    }

    public Consumer<NTScript.Builder> getNTSBuilderContext() {
        return builder -> {
        };
    }

    public void load() {
        skillTrees.clear();
        reloadSkills();
    }

    public Map<String, ISkill> getSkills() {
        return skills;
    }

    public Collection<String> getSkillNames() {
        return skillByNames.keySet();
    }

    public void registerSkillHandler(String key, SkillScriptHandlers toInterface) {
        key = key.toLowerCase();
        skillHandlers.put(key, toInterface);
        Log.info("Registered skill handler " + key + " type " + toInterface.getClass().getSimpleName(), DebugLevel.DEVELOP);
    }

    public SkillScriptHandlers getSkillHandler(String id) {
        id = id.toLowerCase();
        return skillHandlers.get(id);
    }

    public Map<String, SkillTree> getSkillTrees() {
        return skillTrees;
    }

    public SkillResult executeSkill(ActiveCharacter character, PlayerSkillContext esi) {
        if (esi == null) {
            return SkillResult.WRONG_DATA;
        }

        int level = esi.getTotalLevel();
        if (level < 0) {
            return SkillResult.NEGATIVE_SKILL_LEVEL;
        }

        ISkillExecutor skillExecutor = esi.getSkillData().getSkillExecutor();
        if (skillExecutor == null) {
            return SkillResult.NOT_ACTIVE_SKILL;
        }
        return skillExecutor.execute(character, esi);
    }

    public PlayerSkillContext invokeSkillByCombo(String combo, ActiveCharacter character) {
        Map<String, PlayerSkillContext> skills = character.getSkills();
        for (PlayerSkillContext playerSkillContext : skills.values()) {
            if (combo.equals(playerSkillContext.getSkillData().getCombination())) {
                executeSkill(character, playerSkillContext);
                return playerSkillContext;
            }
        }
        return null;
    }

    public void registerAdditionalCatalog(ISkill extraCatalog) {
        if (extraCatalog == null) {
            warn("Cannot register skill null");
            return;
        }
        if (extraCatalog.getId() == null) {
            warn("Cannot register skill " + extraCatalog.getId() + ", " + extraCatalog.getClass().getSimpleName() + " getId() returned"
                    + " null");
            return;
        }
        debug("registering skill " + extraCatalog.getId());
        extraCatalog.init();

        skills.put(extraCatalog.getId().toLowerCase(), extraCatalog);
    }

    public Optional<ISkill> getById(String id) {
        id = id.toLowerCase();
        ISkill skill = skills.get(id);
        return Optional.ofNullable(skill);
    }

    public Collection<ISkill> getAll() {
        return skills.values();
    }

    public ISkill getSkillByLocalizedName(String name) {
        return skillByNames.get(name.toLowerCase());
    }

    public void registerSkillAlternateName(String name, ISkill skill) {
        name = name.toLowerCase();
        if (skills.containsKey(name)) {
            ISkill iSkill = skills.get(name);
            if (iSkill != skill) {
                Log.warn("Alternate name " + name + " for a skill " + skill.getId() + ". But the name is "
                        + "already taken by the skill " + iSkill.getId() + "! If you are reloading you can ignore this message");
            }
        }
        skills.put(name, skill);
        skillByNames.put(name, skill);
    }

    public ISkill skillDefinitionToSkill(ScriptSkillModel scriptSkillModel, ClassLoader classLoader) {

        if (scriptSkillModel.handlerId.equalsIgnoreCase("nts")) {
            try {

                Class c = null;

                String s = String.valueOf(scriptSkillModel.superType).toLowerCase();
                c = getScriptTargetType(c, s);

                if (c == null) {
                    c = SkillScriptHandlers.Active.class;
                    Log.debug("Unknown SuperType " + scriptSkillModel.superType);
                }

                Log.debug("Compiling nts script " + scriptSkillModel.id + " as " + c.getSimpleName());

                Class<? extends SkillScriptHandlers> generate = ntScriptEngine.prepareCompiler(getNTSBuilderContext(), c)
                        .compile(scriptSkillModel.script);
                if (generate == null) {
                    Log.error("Skill - " + scriptSkillModel.id + " Unable to generate script " + scriptSkillModel.id);
                }
                SkillScriptHandlers instance = injector.getInstance(generate);
                ScriptSkill ss = getSkillByHandlerType(instance);
                ss.setModel(scriptSkillModel);
                ss.setHandler(instance);
                if (ss instanceof PassiveScriptSkill p) {
                    p.setRelevantEffectName(scriptSkillModel.relevantEffectName);
                }

                if (scriptSkillModel.skillTypes != null) {
                    for (String skillType : scriptSkillModel.skillTypes) {
                        SkillType st = SkillType.byId(skillType);
                        if (st == null) {
                            Log.warn("Unknown skill type " + st);
                        } else {
                            ss.addSkillType(st);
                        }
                    }
                }

                injector.injectMembers(ss);
                injectCatalogId((ISkill) ss, scriptSkillModel.id);
                return (ISkill) ss;

            } catch (Throwable e) {
                Log.error("Unable to compile script " + scriptSkillModel.id, e);
            }

        }
        return null;
    }

    protected Class getScriptTargetType(Class c, String s) {
        if ("a".equalsIgnoreCase(s) || "active".equalsIgnoreCase(s)) {
            c = SkillScriptHandlers.Active.class;
        } else if ("t".equalsIgnoreCase(s) || "targeted".equalsIgnoreCase(s)) {
            c = SkillScriptHandlers.Targetted.class;
        } else if ("p".equalsIgnoreCase(s) || "passive".equalsIgnoreCase(s)) {
            c = SkillScriptHandlers.Passive.class;
        }
        return c;
    }

    public ScriptSkill getSkillByHandlerType(SkillScriptHandlers instance) {
        if (instance instanceof SkillScriptHandlers.Active) {
            return new ActiveScriptSkill();
        } else if (instance instanceof SkillScriptHandlers.Passive) {
            return new PassiveScriptSkill();
        }
        throw new RuntimeException("Unknown type " + instance);
    }

    public void injectCatalogId(ISkill skill, String name) {
        Optional<Field> first = ClassUtils.getAllFields(skill.getClass()).stream().filter(field -> field.isAnnotationPresent(CatalogId.class))
                .findFirst();
        Field field = first.get();
        field.setAccessible(true);
        try {
            field.set(skill, name);
        } catch (IllegalAccessException e) {
            error("Could not inject CatalogId to the skill", e);
        }
    }

    public Optional<ISkillType> getSkillType(String id) {
        return Optional.ofNullable(skillTypes.get(id.toLowerCase()));
    }

    public void registerSkillType(ISkillType skillType) {
        skillTypes.put(skillType.getId().toLowerCase(), skillType);
    }

    public abstract ISkillTreeInterfaceModel getGuiModelByCharacter(char c);

    public ISkill getSkillById(String id) {
        return getSkills().get(id);
    }

    public CommentedConfig loadAndReturnInternalSkills() {
        String assetAsString = assetService.getAssetAsString("defaults/skills.conf");

        HoconFormat instance = HoconFormat.instance();
        ConfigParser<CommentedConfig> parser = instance.createParser();
        CommentedConfig config = parser.parse(assetAsString);

        loadSkillDefinitionFile(config, this.getClass().getClassLoader());

        return config;
    }

    public void loadSkillDefinitionFile(ClassLoader urlClassLoader, File confFile) {
        info("Loading skills from file " + confFile.getName());
        try (FileConfig fc = FileConfig.of(confFile.getPath())) {
            fc.load();
            loadSkillDefinitionFile(fc, urlClassLoader);
        } catch (Exception e) {
            Log.error("Could not load file " + confFile, e);
        }
    }

    private void loadSkillDefinitionFile(Config config, ClassLoader urlClassLoader) {
        SkillsDefinition definition = new ObjectConverter().toObject(config, SkillsDefinition::new);
        loadSkillDefinitions(urlClassLoader, definition);
    }

    public void loadSkillDefinitions(ClassLoader urlClassLoader, SkillsDefinition definition) {
        if (definition.effects != null) {
            definition.effects.stream()
                    .peek(a -> Log.debug("Compiling Effect " + a.id))
                    .map(a -> EffectScriptGenerator.from(a, urlClassLoader))
                    .filter(Objects::nonNull)
                    .forEach(a -> Rpg.get().getScriptEngine().STL.add(a));
        }
        if (definition.skills != null) {
            definition.skills.stream()
                    .peek(a -> Log.debug("Compiling Skill " + a.id))
                    .map(a -> skillDefinitionToSkill(a, urlClassLoader))
                    .filter(Objects::nonNull)
                    .forEach(this::registerAdditionalCatalog);
        }
        if (definition.listeners != null) {
            definition.listeners.stream()
                    .peek(a -> Log.debug("Compiling Listener " + a.id))
                    .map(a -> ListenerScriptGenerator.from(a, urlClassLoader))
                    .filter(Objects::nonNull)
                    .map(a -> injector.getInstance(a))
                    .forEach(a -> Rpg.get().registerListeners(a));
        }
    }


    public void reloadSkills() {
        CommentedConfig commentedConfig = loadAndReturnInternalSkills();

        Path addonDir = Paths.get(Rpg.get().getWorkingDirectory() + File.separator + "addons");

        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{}, this.getClass().getClassLoader());

        File file1 = addonDir.toFile();
        File[] files = file1.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".conf"));
        if (files != null) {
            for (File confFile : files) {
                info("Loading file " + confFile);
                loadSkillDefinitionFile(urlClassLoader, confFile);
            }
        }

        dumpSkillConfigOptions(commentedConfig);
    }

    private void dumpSkillConfigOptions(CommentedConfig commentedConfig) {
        try {
            SkillsDefinition definition = new ObjectConverter().toObject(commentedConfig, SkillsDefinition::new);

            List<SkillDumpConfiguration> configurations = new ArrayList<>();
            for (ISkill value : skills.values()) {

                SkillDumpConfiguration skillDumpConfiguration = new SkillDumpConfiguration();
                skillDumpConfiguration.setSkillId(value.getId());

                SkillSettings defaultSkillSettings = value.getDefaultSkillSettings();

                Map<String, String> nodes = defaultSkillSettings.getNodes();
                for (Map.Entry<String, String> e : nodes.entrySet()) {
                    skillDumpConfiguration.add(e.getKey());
                }
                Optional<ScriptSkillModel> first = definition.skills.stream().filter(a -> a.id.equals(value.getId())).findFirst();
                if (first.isPresent()) {
                    ScriptSkillModel scriptSkillModel = first.get();
                    String script = scriptSkillModel.script;
                    if (script != null) {
                        Pattern pattern = Pattern.compile("\\$settings\\.([a-zA-Z0-9_-]+)");
                        Matcher matcher = pattern.matcher(script);
                        while (matcher.find()) {
                            skillDumpConfiguration.add(matcher.group(1));
                        }
                    }
                }
                configurations.add(skillDumpConfiguration);
            }

            configurations.sort(Comparator.comparing(SkillDumpConfiguration::getSkillId));

            Path dump = Paths.get(Rpg.get().getWorkingDirectory() + File.separator + "skills_dump.conf");
            try (FileConfig fc = FileConfig.of(dump)) {
                new ObjectConverter().toConfig(new SkillDumpConfigurations(configurations), fc);
                fc.save();
            }
        } catch (Exception e) {
            Log.error("Could not create skills_dump.conf", e);
        }
    }

    public void loadSkilltree(Runnable r) {

    }
}
