package cz.neumimto.rpg.common.skills;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.scripting.ActiveScriptSkill;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.PassiveScriptSkill;
import cz.neumimto.rpg.api.skills.types.ScriptSkill;
import cz.neumimto.rpg.api.utils.ClassUtils;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.utils.annotations.CatalogId;
import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static cz.neumimto.rpg.api.logging.Log.*;

public abstract class AbstractSkillService implements SkillService {

    protected Map<String, ISkill> skills = new HashMap<>();
    protected Map<String, SkillTree> skillTrees = new ConcurrentHashMap<>();
    protected Map<String, ISkill> skillByNames = new HashMap<>();
    protected Map<String, ISkillType> skillTypes = new HashMap<>();

    @Inject
    private SkillTreeDao skillTreeDao;

    @Inject
    private ClassService classService;

    @Inject
    private Injector injector;

    @Inject
    private CustomSkillGenerator customSkillGenerator;

    private Map<String, SkillScriptHandlers> skillHandlers = new HashMap<>();

    public AbstractSkillService() {
        Stream.of(SkillType.values()).forEach(this::registerSkillType);
    }

    @Override
    public void load() {
        skillTrees.clear();
        skillTrees.putAll(skillTreeDao.getAll());
    }

    @Override
    public Map<String, ISkill> getSkills() {
        return skills;
    }

    @Override
    public Collection<String> getSkillNames() {
        return skillByNames.keySet();
    }

    @Override
    public void registerSkillHandler(String key, SkillScriptHandlers toInterface) {
        key = key.toLowerCase();
        skillHandlers.put(key, toInterface);
        Log.info("Registered skill handler " + key + " type " + toInterface.getClass().getSimpleName(), DebugLevel.DEVELOP);
    }

    @Override
    public SkillScriptHandlers getSkillHandler(String id) {
        id = id.toLowerCase();
        return skillHandlers.get(id);
    }

    @Override
    public Map<String, SkillTree> getSkillTrees() {
        return skillTrees;
    }

    @Override
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

    @Override
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

    @Override
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
        extraCatalog.init();

        skills.put(extraCatalog.getId().toLowerCase(), extraCatalog);
    }

    @Override
    public Optional<ISkill> getById(String id) {
        id = id.toLowerCase();
        ISkill skill = skills.get(id);
        return Optional.ofNullable(skill);
    }

    @Override
    public Collection<ISkill> getAll() {
        return skills.values();
    }

    @Override
    public ISkill getSkillByLocalizedName(String name) {
        return skillByNames.get(name.toLowerCase());
    }

    @Override
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

    @Override
    public ISkill skillDefinitionToSkill(ScriptSkillModel scriptSkillModel, ClassLoader classLoader) {

        if (scriptSkillModel.getHandlerId().equalsIgnoreCase("custom")) {
            try {
                Class<? extends ISkill> generate = customSkillGenerator.generate(scriptSkillModel, classLoader);
                if (generate == null) {
                    Log.error("Unable to generate skill " + scriptSkillModel.getId());
                }
                return injector.getInstance(generate);
            } catch (Exception e) {
                Log.error("Unable to generate skill " + scriptSkillModel.getId(), e);
            }
        } else {
            SkillScriptHandlers type = getSkillHandler(scriptSkillModel.getHandlerId());
            if (type == null) {
                warn("Could not load skill " + scriptSkillModel.getId() + " unknown handler " + scriptSkillModel.getHandlerId());
                return null;
            }

            //todo
            Class subClass = null;
            if (type instanceof SkillScriptHandlers.Active) {
                subClass = ActiveScriptSkill.class;
            } else if (type instanceof SkillScriptHandlers.Passive) {
                subClass = PassiveScriptSkill.class;
            } else if (type instanceof SkillScriptHandlers.Targetted) {
                //todo
            }

            String name = scriptSkillModel.getId();
            name = name.replaceAll("[\\W]", "");
            Class sk = new ByteBuddy()
                    .subclass(subClass)
                    .name("cz.neumimto.skills.scripts." + name)
                    .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                            .define("value", scriptSkillModel.getId())
                            .build())
                    .make()
                    .load(classLoader)
                    .getLoaded();
            try {

                ScriptSkill s = (ScriptSkill) injector.getInstance(sk);
                s.setHandler(type);
                SkillSettings settings = new SkillSettings();
                ((ISkill) s).setSettings(settings);
                injectCatalogId((ISkill) s, scriptSkillModel.getId());
                s.setModel(scriptSkillModel);
                if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
                    info("-------- Created skill from skill def.");
                    info("+ ClassName " + s.getClass().getName());
                    info("+ ClassLoader " + s.getClass().getClassLoader());
                    info("+ Handler Id " + s.getModel().getHandlerId());
                    info("+ Type " + subClass.getSimpleName());
                }
                return (ISkill) s;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
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

    @Override
    public Optional<ISkillType> getSkillType(String id) {
        return Optional.ofNullable(skillTypes.get(id.toLowerCase()));
    }

    @Override
    public void registerSkillType(ISkillType skillType) {
        skillTypes.put(skillType.getId().toLowerCase(), skillType);
    }

}
