package cz.neumimto.rpg.common.skills;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.google.inject.Injector;
import cz.neumimto.nts.NTScript;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.configuration.SkillTreeDao;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.scripting.NTScriptEngine;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.scripting.ActiveScriptSkill;
import cz.neumimto.rpg.common.skills.scripting.PassiveScriptSkillHandler;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.common.skills.types.PassiveScriptSkill;
import cz.neumimto.rpg.common.skills.types.ScriptSkill;
import cz.neumimto.rpg.common.utils.ClassUtils;
import cz.neumimto.rpg.common.utils.DebugLevel;
import cz.neumimto.rpg.common.utils.annotations.CatalogId;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static cz.neumimto.rpg.common.logging.Log.*;

public abstract class SkillService {

    protected Map<String, ISkill> skills = new HashMap<>();
    protected Map<String, SkillTree> skillTrees = new ConcurrentHashMap<>();
    protected Map<String, ISkill> skillByNames = new HashMap<>();
    protected Map<String, ISkillType> skillTypes = new HashMap<>();

    @Inject
    protected SkillTreeDao skillTreeDao;

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

    public void load() {
        skillTrees.clear();
        skillTrees.putAll(skillTreeDao.getAll());
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

    public SkillResult executeSkill(IActiveCharacter character, PlayerSkillContext esi) {
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

    public PlayerSkillContext invokeSkillByCombo(String combo, IActiveCharacter character) {
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
        info("registering skill " + extraCatalog.getId(), DebugLevel.DEVELOP);
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


    public abstract NTScript getNtScriptCompilerFor(Class<? extends SkillScriptHandlers> c);

    public ISkill skillDefinitionToSkill(ScriptSkillModel scriptSkillModel, ClassLoader classLoader) {

        if (scriptSkillModel.getHandlerId().equalsIgnoreCase("nts")) {
            try {

                Class c = null;

                String s = String.valueOf(scriptSkillModel.getSuperType()).toLowerCase();
                if ("a".equalsIgnoreCase(s) || "active".equalsIgnoreCase(s)) {
                    c = SkillScriptHandlers.Active.class;
                } else if ("t".equalsIgnoreCase(s) || "targeted".equalsIgnoreCase(s)) {
                    c = SkillScriptHandlers.Targetted.class;
                } else if ("p".equalsIgnoreCase(s) || "passive".equalsIgnoreCase(s)) {
                    c = SkillScriptHandlers.Passive.class;
                }

                if (c == null) {
                    c = SkillScriptHandlers.Active.class;
                    Log.info("Unknown SuperType " + scriptSkillModel.getSuperType());
                }

                Log.info("Compiling nts script " + scriptSkillModel.getId() + " as " + c.getSimpleName());

                Class<? extends SkillScriptHandlers> generate = getNtScriptCompilerFor(c).compile(scriptSkillModel.getScript());
                if (generate == null) {
                    Log.error("Unable to generate script " + scriptSkillModel.getId());
                }
                SkillScriptHandlers instance = injector.getInstance(generate);
                ScriptSkill ss = getSkillByHandlerType(instance);
                ss.setModel(scriptSkillModel);
                ss.setHandler(instance);
                injector.injectMembers(ss);
                injectCatalogId((ISkill) ss, scriptSkillModel.getId());
                return (ISkill) ss;

            } catch (Throwable e) {
                Log.error("Unable to compile script " + scriptSkillModel.getId(), e);
            }

        }
        return null;
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

    public void loadInternalSkills() {
        String assetAsString = assetService.getAssetAsString("defaults/skills.conf");

        HoconFormat instance = HoconFormat.instance();
        ConfigParser<CommentedConfig> parser = instance.createParser();
        CommentedConfig config = parser.parse(assetAsString);

        loadSkillDefinitionFile(config, this.getClass().getClassLoader());
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
        definition.getSkills().stream()
                .map(a -> skillDefinitionToSkill(a, urlClassLoader))
                .filter(Objects::nonNull)
                .forEach(a -> registerAdditionalCatalog(a));
    }

    public void reloadSkills() {
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

        loadInternalSkills();
    }
}
