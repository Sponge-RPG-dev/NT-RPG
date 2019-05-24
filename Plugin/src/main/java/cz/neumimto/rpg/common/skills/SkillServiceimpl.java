package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.ClassService;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.scripting.ActiveScriptSkill;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.PassiveScriptSkill;
import cz.neumimto.rpg.api.skills.types.ScriptSkill;
import cz.neumimto.rpg.api.skills.types.TargetedScriptSkill;
import cz.neumimto.rpg.common.utils.annotations.CatalogId;
import cz.neumimto.rpg.persistance.SkillTreeDao;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.sponge.gui.SkillTreeInterfaceModel;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;
import static cz.neumimto.rpg.api.logging.Log.*;

public abstract class SkillServiceimpl implements ISkillService {

    @Inject
    private SkillTreeDao skillTreeDao;

    @Inject
    private ClassService classService;

    @Inject
    private JSLoader jsLoader;

    @Inject
    private CharacterService characterService;

    protected Map<String, ISkill> skills = new HashMap<>();

    protected Map<String, SkillTree> skillTrees = new ConcurrentHashMap<>();

    protected Map<Character, SkillTreeInterfaceModel> guiModelByCharacter = new HashMap<>();

    protected Map<Short, SkillTreeInterfaceModel> guiModelById = new HashMap<>();

    protected Map<String, ISkill> skillByNames = new HashMap<>();


    @Override
    public void load() {
        initGuis();
        skillTrees.putAll(skillTreeDao.getAll());
    }

    @Override
    public Map<String, ISkill> getSkills() {
        return skills;
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
        Long aLong = character.getCooldowns().get(esi.getSkill().getName());
        long servertime = System.currentTimeMillis();
        if (aLong != null && aLong > servertime) {
            Gui.sendCooldownMessage(character, esi.getSkill().getName(), ((aLong - servertime) / 1000.0));
            callback.doNext(character, esi, new SkillContext().result(SkillResult.ON_COOLDOWN));
            return;
        }

        SkillContext context = esi.getSkill().createSkillExecutorContext(esi);

        context.addExecutor(SkillPreprocessors.SKILL_COST);
        context.addExecutor(callback);
        //skill execution startEffectScheduler
        esi.getSkill().onPreUse(character, context);
        //skill execution stopEffectScheduler
    }

    @Override
    public PlayerSkillContext invokeSkillByCombo(String combo, IActiveCharacter character) {
        for (PlayerSkillContext playerSkillContext : character.getSkills().values()) {
            if (combo.equals(playerSkillContext.getSkillData().getCombination())) {
                executeSkill(character, playerSkillContext, new SkillExecutorCallback());
                return playerSkillContext;
            }
        }
        return null;
    }


    @Override
    public void reloadSkillTrees() {
        try {
            info("Currently its possible to reload ascii maps or add new skill trees");
            Map<String, SkillTree> all = skillTreeDao.getAll();
            for (Map.Entry<String, SkillTree> s : all.entrySet()) {
                SkillTree skillTree = skillTrees.get(s.getKey());
                if (skillTree == null) {
                    skillTrees.put(s.getValue().getId(), s.getValue());
                    info("Found new Skilltree " + s.getValue().getId());
                } else {
                    skillTree.setSkillTreeMap(s.getValue().getSkillTreeMap());
                    skillTree.setCenter(s.getValue().getCenter());
                    info("Refreshed skilltree view for " + s.getValue().getId());
                }
            }

			/* todo thats gonna be quite tricky,
			   todo  it should be easiest to lock (maybe even joining) specific commands,  save all current data, reset player objects, and recreate
			    ActiveCharacters
			for (IActiveCharacter character : characterService.getCharacters()) {
				Set<ExtendedNClass> classes = character.getClasses();
				for (ExtendedNClass aClass : classes) {
					SkillTree skillTree = aClass.getClassDefinition().getSkillTree();
					if (skillTree == null) continue; //should not happen anyway
					String id = skillTree.getId();
					SkillTree skillTree1 = skillTrees.get(id);
					if (skillTree1 == null) continue;
					aClass.getClassDefinition().setSkillTree(skillTree1);

					aClass.getClassDefinition().
				}
			}
			*/
        } catch (Exception e) {
            warn("Failed to reload skilltrees: " + e.getMessage());
        }
    }

    @Override
    public SkillTreeInterfaceModel getGuiModelByCharacter(Character character) {
        return guiModelByCharacter.get(character);
    }

    @Override
    public SkillTreeInterfaceModel getGuiModelById(Short k) {
        return guiModelById.get(k);
    }

    @Override
    public void registerAdditionalCatalog(ISkill extraCatalog) {
        if (extraCatalog.getId() == null) {
            warn("Cannot register skill " + extraCatalog.getName() + ", " + extraCatalog.getClass().getSimpleName() + " getId() returned"
                    + " null");
            return;
        }
        extraCatalog.init();

        skills.put(extraCatalog.getId().toLowerCase(), extraCatalog);
        skillByNames.put(extraCatalog.getName(), extraCatalog);
        if (extraCatalog.getLocalizableName() != null) {
            skillByNames.put(extraCatalog.getLocalizableName(), extraCatalog);
        }
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
        return skillByNames.get(name);
    }

    @Override
    public void registerSkillAlternateName(String name, ISkill skill) {
        if (skillByNames.containsKey(name)) {
            ISkill iSkill = skillByNames.get(name);
            if (iSkill != skill) {
                throw new RuntimeException("Attempted to register alternate name " + name + " for a skill " + skill.getId() + ". But the name is "
                        + "already taken by the skill " + iSkill.getId());
            }
            Log.warn("Attempted to register alternate name for a skill " + skill.getId() + ". Skill is already registered under the same name - " + name);
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

        Class type = null;
        switch (parent.toLowerCase()) {
            case "targetted":
                type = TargetedScriptSkill.class;
                break;
            case "active":
                type = ActiveScriptSkill.class;
                break;
            case "passive":
                type = PassiveScriptSkill.class;
                break;
            default:
                warn("Could not load skill " + scriptSkillModel.getId() + " unknown parent " + scriptSkillModel.getParent());
                return null;
        }

        Class sk = new ByteBuddy()
                .subclass(type)
                .name("cz.neumimto.skills.scripts." + scriptSkillModel.getName())
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", scriptSkillModel.getId())
                        .build())
                .make()
                .load(classLoader)
                .getLoaded();
        try {

            ScriptSkill s = (ScriptSkill) sk.newInstance();

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
            if (pluginConfig.DEBUG.isDevelop()) {
                info("-------- Created skill from skill def.");
                info("+ ClassName " + s.getClass().getName());
                info("+ ClassLoader " + s.getClass().getClassLoader());
                info("+ Script:\n " + s.bindScriptToTemplate(scriptSkillModel));
            }
            return (ISkill) s;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void injectCatalogId(ISkill skill, String name) {
        Optional<Field> first = Stream.of(FieldUtils.getAllFields(skill.getClass())).filter(field -> field.isAnnotationPresent(CatalogId.class))
                .findFirst();
        Field field = first.get();
        field.setAccessible(true);
        try {
            field.set(skill, name);
        } catch (IllegalAccessException e) {
            error("Could not inject CatalogId to the skill", e);
        }
    }
}
