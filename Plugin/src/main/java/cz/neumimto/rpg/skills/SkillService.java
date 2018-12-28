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

import static cz.neumimto.rpg.Log.error;
import static cz.neumimto.rpg.Log.info;
import static cz.neumimto.rpg.Log.warn;
import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.gui.SkillTreeInterfaceModel;
import cz.neumimto.rpg.persistance.SkillTreeDao;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.reloading.Reload;
import cz.neumimto.rpg.reloading.ReloadService;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.configs.ScriptSkillModel;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.skills.mods.SkillPreprocessors;
import cz.neumimto.rpg.skills.parents.ActiveScriptSkill;
import cz.neumimto.rpg.skills.parents.PassiveScriptSkill;
import cz.neumimto.rpg.skills.parents.ScriptSkill;
import cz.neumimto.rpg.skills.parents.TargettedScriptSkill;
import cz.neumimto.rpg.skills.tree.SkillTree;
import cz.neumimto.rpg.utils.CatalogId;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by NeumimTo on 1.1.2015.
 */
@Singleton
public class SkillService implements AdditionalCatalogRegistryModule<ISkill> {

	private static int id = 0;

	@Inject
	private SkillTreeDao skillTreeDao;

	@Inject
	private GroupService groupService;

	@Inject
	private JSLoader jsLoader;

	@Inject
	private Game game;

	@Inject
	private CharacterService characterService;

	@RegisterCatalog(ISkill.class)
	private Map<String, ISkill> skills = new HashMap<>();

	private Map<String, SkillTree> skillTrees = new ConcurrentHashMap<>();

	private Map<Character, SkillTreeInterfaceModel> guiModelByCharacter = new HashMap<>();

	private Map<Short, SkillTreeInterfaceModel> guiModelById = new HashMap<>();

	private Map<String, ISkill> skillByNames = new HashMap<>();

	public void load() {
		initGuis();
		skillTrees.putAll(skillTreeDao.getAll());
	}

	@Reload(on = ReloadService.PLUGIN_CONFIG)
	public void initGuis() {
		int i = 0;

		for (String str : pluginConfig.SKILLTREE_RELATIONS) {
			String[] split = str.split(",");

			short k = (short) (Short.MAX_VALUE - i);
			SkillTreeInterfaceModel model = new SkillTreeInterfaceModel(Integer.parseInt(split[3]),
					Sponge.getRegistry().getType(ItemType.class, split[1]).orElse(ItemTypes.STICK),
					split[2], k);

			guiModelById.put(k, model);
			guiModelByCharacter.put(split[0].charAt(0), model);
			i++;
		}

	}

	public Map<String, ISkill> getSkills() {
		return skills;
	}

	public Map<String, SkillTree> getSkillTrees() {
		return skillTrees;
	}

	public void executeSkill(IActiveCharacter character, ISkill skill, SkillExecutorCallback callback) {
		if (character.hasSkill(skill.getId())) {
			executeSkill(character, character.getSkillInfo(skill), callback);
		}
	}

	public void executeSkill(IActiveCharacter character, ExtendedSkillInfo esi, SkillExecutorCallback callback) {
		if (esi == null) {
			callback.doNext(character, null, new SkillContext().result(SkillResult.WRONG_DATA));
			return;
		}
		SkillContext context = esi.getSkill().createSkillExecutorContext(esi);
		int level = esi.getTotalLevel();
		if (level < 0) {
			callback.doNext(character, esi, context.result(SkillResult.NEGATIVE_SKILL_LEVEL));
			return;
		}
		Long aLong = character.getCooldowns().get(esi.getSkill().getName());
		long servertime = System.currentTimeMillis();
		if (aLong != null && aLong > servertime) {
			Gui.sendCooldownMessage(character, esi.getSkill().getName(), ((aLong - servertime) / 1000.0));
			callback.doNext(character, esi, context.result(SkillResult.ON_COOLDOWN));
			return;
		}

		context.addExecutor(SkillPreprocessors.SKILL_COST);
		context.addExecutor(callback);
		//skill execution start
		esi.getSkill().onPreUse(character, context);
		//skill execution sto
	}

	public ExtendedSkillInfo invokeSkillByCombo(String combo, IActiveCharacter character) {
		for (ExtendedSkillInfo extendedSkillInfo : character.getSkills().values()) {
			if (combo.equals(extendedSkillInfo.getSkillData().getCombination())) {
				executeSkill(character, extendedSkillInfo, new SkillExecutorCallback());
				return extendedSkillInfo;
			}
		}
		return null;
	}


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
					SkillTree skillTree = aClass.getConfigClass().getSkillTree();
					if (skillTree == null) continue; //should not happen anyway
					String id = skillTree.getId();
					SkillTree skillTree1 = skillTrees.get(id);
					if (skillTree1 == null) continue;
					aClass.getConfigClass().setSkillTree(skillTree1);

					aClass.getConfigClass().
				}
			}
			*/
		} catch (Exception e) {
			warn("Failed to reload skilltrees: " + e.getMessage());
		}
	}

	public SkillTreeInterfaceModel getGuiModelByCharacter(Character character) {
		return guiModelByCharacter.get(character);
	}

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
		skillByNames.put(extraCatalog.getLocalizableName().toPlain(), extraCatalog);
	}

	@Override
	public Optional<ISkill> getById(String id) {
		id = id.toLowerCase();
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

	public ISkill getSkillByLocalizedName(String name) {
		return skillByNames.get(name);
	}

	public void registerSkillAlternateName(String name, ISkill skill) {
		if (skillByNames.containsKey(name)) {
			throw new RuntimeException("Attempted to register altername name " + name + " for a skill " + skill.getId() + ". But the name is "
					+ "already taken by the skill " + skillByNames.get(name).getId());
		}
		skillByNames.put(name, skill);
	}

	public ISkill skillDefinitionToSkill(ScriptSkillModel scriptSkillModel, ClassLoader classLoader) {
		String parent = scriptSkillModel.getParent();
		if (parent == null) {
			warn("Could not load skill " + scriptSkillModel.getId() + " missing parent node");
			return null;
		}

		Class type = null;
		switch (parent.toLowerCase()) {
			case "targetted":
				type = TargettedScriptSkill.class;
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
				.name("cz.neumimto.skills.scripts." + scriptSkillModel.getName().toPlain())
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
			IoC.get().get(sk, s);
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
