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

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.events.skills.SkillPostUsageEvent;
import cz.neumimto.rpg.events.skills.SkillPrepareEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.gui.SkillTreeInterfaceModel;
import cz.neumimto.rpg.inventory.sockets.SocketType;
import cz.neumimto.rpg.persistance.SkillTreeDao;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.reloading.Reload;
import cz.neumimto.rpg.reloading.ReloadService;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.configs.ScriptSkillModel;
import cz.neumimto.rpg.skills.parents.ActiveScriptSkill;
import cz.neumimto.rpg.skills.parents.ScriptSkill;
import cz.neumimto.rpg.skills.parents.TargettedScriptSkill;
import cz.neumimto.rpg.skills.tree.SkillTree;
import cz.neumimto.rpg.utils.CatalogId;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

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

		for (String str : PluginConfig.SKILLTREE_RELATIONS) {
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

	public SkillResult executeSkill(IActiveCharacter character, ISkill skill) {
		if (character.hasSkill(skill.getId())) {
			return executeSkill(character, character.getSkillInfo(skill));
		}
		return SkillResult.WRONG_DATA;
	}

	public SkillResult executeSkill(IActiveCharacter character, ExtendedSkillInfo esi) {
		if (esi == null) {
			return SkillResult.FAIL;
		}
		int level = esi.getTotalLevel();
		if (level < 0) {
			return SkillResult.NEGATIVE_SKILL_LEVEL;
		}
		level += characterService.getCharacterProperty(character, DefaultProperties.all_skills_bonus);
		Long aLong = character.getCooldowns().get(esi.getSkill().getName());
		long servertime = System.currentTimeMillis();
		if (aLong != null && aLong > servertime) {
			Gui.sendCooldownMessage(character, esi.getSkill().getName(), ((aLong - servertime) / 1000.0));
			return SkillResult.ON_COOLDOWN;
		}
		SkillData skillData = esi.getSkillData();
		SkillSettings skillSettings = skillData.getSkillSettings();
		float requiredMana = skillSettings.getLevelNodeValue(SkillNodes.MANACOST, level);
		float requiredHp = skillSettings.getLevelNodeValue(SkillNodes.HPCOST, level);
		SkillPrepareEvent event = new SkillPrepareEvent(character, requiredHp, requiredMana);
		game.getEventManager().post(event);
		if (event.isCancelled()) {
			return SkillResult.FAIL;
		}
		double hpcost = event.getRequiredHp() * characterService.getCharacterProperty(character, DefaultProperties.health_cost_reduce);
		double manacost = event.getRequiredMana() * characterService.getCharacterProperty(character, DefaultProperties.mana_cost_reduce);
		//todo float staminacost =
		if (character.getHealth().getValue() > hpcost) {
			if (character.getMana().getValue() >= manacost) {
				SkillResult result = esi.getSkill().onPreUse(character);
				if (result != SkillResult.OK) {
					return result;
				} else {
					float newCd = skillSettings.getLevelNodeValue(SkillNodes.COOLDOWN, level);
					SkillPostUsageEvent eventt = new SkillPostUsageEvent(character, hpcost, manacost, newCd);
					game.getEventManager().post(eventt);
					if (!event.isCancelled()) {
						double newval = character.getHealth().getValue() - eventt.getHpcost();
						if (newval <= 0) {
							//todo kill the player ?
							HealthData healthData = character.getPlayer().getHealthData();
						} else {
							character.getHealth().setValue(newval);
							newCd = eventt.getCooldown() * characterService.getCharacterProperty(character, DefaultProperties.cooldown_reduce);
							character.getMana().setValue(character.getMana().getValue() - event.getRequiredMana());
							long cd = (long) newCd;
							character.getCooldowns().put(esi.getSkill().getName(), cd + servertime);
							Gui.displayMana(character);
							return SkillResult.OK;
						}
					}
				}
			}
			return SkillResult.NO_MANA;
		}
		return SkillResult.NO_HP;
	}

	public void invokeSkillByCombo(String combo, IActiveCharacter character) {
		for (ExtendedSkillInfo extendedSkillInfo : character.getSkills().values()) {
			if (combo.equals(extendedSkillInfo.getSkillData().getCombination())) {
				character.sendMessage(ChatTypes.ACTION_BAR,
						Text.builder(extendedSkillInfo.getSkill().getName())
								.style(TextStyles.BOLD)
								.color(TextColors.GOLD)
								.build()
				);
				executeSkill(character, extendedSkillInfo);
				break;
			}
		}
		Gui.displayCurrentClicks(character, combo);
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
			((ISkill)s).setSettings(settings);
			injectCatalogId((ISkill) s, scriptSkillModel.getId());
			s.setModel(scriptSkillModel);
			IoC.get().get(sk, s);
			s.initScript();
			if (PluginConfig.DEBUG.isDevelop()) {
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
