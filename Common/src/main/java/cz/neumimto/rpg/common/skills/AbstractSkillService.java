package cz.neumimto.rpg.common.skills;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;
import static cz.neumimto.rpg.api.logging.Log.warn;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.scripting.ActiveScriptSkill;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.PassiveScriptSkill;
import cz.neumimto.rpg.api.skills.types.ScriptSkill;
import cz.neumimto.rpg.api.utils.ClassUtils;
import cz.neumimto.rpg.api.utils.annotations.CatalogId;
import cz.neumimto.rpg.common.skills.preprocessors.SkillPreprocessors;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.inject.Inject;

public abstract class AbstractSkillService implements SkillService {

    protected Map<String, ISkill> skills = new HashMap<>();
    protected Map<String, SkillTree> skillTrees = new ConcurrentHashMap<>();
    protected Map<String, ISkill> skillByNames = new HashMap<>();
    protected Map<String, ISkillType> skillTypes = new HashMap<>();
    protected Map<String, Class<?>> scriptSkillsParents = new HashMap<>();

    @Inject
    private SkillTreeDao skillTreeDao;

    @Inject
    private ClassService classService;

    @Inject
    private Injector injector;

    @Override
    public void load() {
        scriptSkillsParents.put("active", ActiveScriptSkill.class);
        scriptSkillsParents.put("passive", PassiveScriptSkill.class);

        Stream.of(SkillType.values()).forEach(this::registerSkillType);

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
    public Map<String, SkillTree> getSkillTrees() {
        return skillTrees;
    }

    @Override
    public void executeSkill(IActiveCharacter character, ISkill skill, SkillExecutorCallback callback) {
        if (character.hasSkill(skill.getId())) {
            executeSkill(character, character.getSkillInfo(skill), callback);
        }
    }

    @Override
    public void executeSkill(IActiveCharacter character, PlayerSkillContext esi, SkillExecutorCallback callback) {
        if (esi == null) {
            callback.doNext(character, null, new SkillContext().result(SkillResult.WRONG_DATA));
            return;
        }

        int level = esi.getTotalLevel();
        if (level < 0) {
            callback.doNext(character, esi, new SkillContext().result(SkillResult.NEGATIVE_SKILL_LEVEL));
            return;
        }

        Long aLong = character.getCooldown(esi.getSkill().getId());
        long servertime = System.currentTimeMillis();
        if (aLong != null && aLong > servertime) {
            Gui.sendCooldownMessage(character, esi.getSkillData().getSkillName(), ((aLong - servertime) / 1000.0));
            callback.doNext(character, esi, new SkillContext().result(SkillResult.ON_COOLDOWN));
            return;
        }

        SkillContext context = esi.getSkill().createSkillExecutorContext(esi);

        executeSkill(character, esi, context, callback);
    }

    @Override
    public void executeSkill(IActiveCharacter character, PlayerSkillContext esi, SkillContext context,
                              SkillExecutorCallback callback) {

        context.addExecutor(SkillPreprocessors.SKILL_COST);
        context.addExecutor(callback);
        //skill execution start
        esi.getSkill().onPreUse(character, context);
        //skill execution stop
    }

    @Override
    public PlayerSkillContext invokeSkillByCombo(String combo, IActiveCharacter character) {
        Map<String, PlayerSkillContext> skills = character.getSkills();
        for (PlayerSkillContext playerSkillContext : skills.values()) {
            if (combo.equals(playerSkillContext.getSkillData().getCombination())) {
                executeSkill(character, playerSkillContext, new SkillExecutorCallback());
                return playerSkillContext;
            }
        }
        return null;
    }

    @Override
    public void registerAdditionalCatalog(ISkill extraCatalog) {
        if (extraCatalog == null) {
            warn("Cannot register skill null");
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
        ISkill skill = skills.get(id);
        if (skill == null) {
            skill = getSkillByLocalizedName(id);
        }
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
        if (skillByNames.containsKey(name)) {
            ISkill iSkill = skillByNames.get(name);
            if (iSkill != skill) {
                throw new RuntimeException("Attempted to register alternate name " + name + " for a skill " + skill.getId() + ". But the name is "
                        + "already taken by the skill " + iSkill.getId());
            }
            warn("Attempted to register alternate name for a skill " + skill.getId() + ". Skill is already registered under the same name - " + name);
        }
        skillByNames.put(name, skill);
    }

    @Override
    public ISkill skillDefinitionToSkill(ScriptSkillModel scriptSkillModel, ClassLoader classLoader) {
        String parent = scriptSkillModel.getParent();
        if (parent == null) {
            warn("Could not load skill " + scriptSkillModel.getId() + " missing parent node");
            return null;
        }

        Class type = scriptSkillsParents.get(parent.toLowerCase());
        if (type == null) {
            warn("Could not load skill " + scriptSkillModel.getId() + " unknown parent " + scriptSkillModel.getParent());
            return null;
        }

        String name = scriptSkillModel.getId();
        name = name.replaceAll("[\\W]", "");
        Class sk = new ByteBuddy()
                .subclass(type)
                .name("cz.neumimto.skills.scripts." + name)
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", scriptSkillModel.getId())
                        .build())
                .make()
                .load(classLoader)
                .getLoaded();
        try {

            ScriptSkill s = (ScriptSkill) injector.getInstance(sk);

            SkillSettings settings = new SkillSettings();
            Map<String, Float> settings2 = scriptSkillModel.getSettings();
            for (Map.Entry<String, Float> w : settings2.entrySet()) {
                settings.addNode(w.getKey(), w.getValue());
            }
            ((ISkill) s).setSettings(settings);
            injectCatalogId((ISkill) s, scriptSkillModel.getId());
            s.setModel(scriptSkillModel);
            //	IoC.get().get(sk, s);
            s.initScript();
            if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
                info("-------- Created skill from skill def.");
                info("+ ClassName " + s.getClass().getName());
                info("+ ClassLoader " + s.getClass().getClassLoader());
                info("+ Script:\n " + s.bindScriptToTemplate(scriptSkillModel));
            }
            return (ISkill) s;
        } catch (Exception e) {
            e.printStackTrace();
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
