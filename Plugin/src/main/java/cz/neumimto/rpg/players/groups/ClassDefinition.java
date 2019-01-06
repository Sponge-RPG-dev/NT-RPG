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

package cz.neumimto.rpg.players.groups;

import cz.neumimto.rpg.configuration.adapters.AllowedArmorListAdapter;
import cz.neumimto.rpg.configuration.adapters.ClassLevelingDefinitionAdapter;
import cz.neumimto.rpg.configuration.adapters.ClassTypeAdapter;
import cz.neumimto.rpg.configuration.adapters.PropertyMapAdapter;
import cz.neumimto.rpg.configuration.adapters.SkillTreeLookupAdapter;
import cz.neumimto.rpg.configuration.adapters.WeaponsAdapter;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.players.ExperienceSource;
import cz.neumimto.rpg.players.leveling.ClassLevelingDefinition;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.skills.tree.SkillTree;
import ninja.leaping.configurate.objectmapping.Adapter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by NeumimTo on 27.12.2014.
 */
@ConfigSerializable
public class ClassDefinition /* implements IEffectSourceProvider */ {

	@Setting("Name")
	private String name;

	@Setting("Description")
	private String description;

	@Setting("PreferredTextColor")
	private TextColor preferedColor;

	@Setting("ItemInfo")
	private ItemStack info;

	@Setting("ItemType")
	private ItemType itemType;

	@Setting("Visible")
	private boolean showsInMenu = true;

	@Setting("ClassType")
	@Adapter(ClassTypeAdapter.class)
	protected String type;

	@Setting("Properties")
	@Adapter(PropertyMapAdapter.class)
	private Map<Integer, Float> propBonus = new HashMap<>();

	@Setting
	@Adapter(AllowedArmorListAdapter.class)
	private Set<RPGItemType> allowedArmor = new HashSet<>();

	@Setting("Permissions")
	private TreeSet<PlayerGroupPermission> permissions = new TreeSet<>();

	@Setting("Properties")
	@Adapter(PropertyMapAdapter.class)
	private Map<Integer, Float> propLevelBonus = new HashMap<>();

	@Setting("ExitCommands")
	private List<String> exitCommands;

	@Setting("EnterCommands")
	private List<String> enterCommands;

	@Setting("ProjectileDamage")
	private Map<EntityType, Double> projectileDamage = new HashMap<>();

	@Setting("Weapons")
	@Adapter(WeaponsAdapter.class)
	private HashMap<ItemType, Set<ConfigRPGItemType>> weapons = new HashMap<>();

	private Map<ICharacterAttribute, Integer> startingAttributes = new HashMap<>();

	private Map<IGlobalEffect, EffectParams> effects = new HashMap<>();

	private HashMap<ItemType, Set<ConfigRPGItemType>> offHandWeapons = new HashMap<>();

	private Map<String, Map<EntityType, Double>> experiences = new HashMap<>();

	@Setting("SkillTreeId")
	@Adapter(SkillTreeLookupAdapter.class)
	private SkillTree skillTree;

	@Setting("SkillPointsPerLevel")
	private int skillpointsperlevel;

	@Setting("AttributePointsPerLevel")
	private int attributepointsperlevel;

	@Setting("Leveling")
	@Adapter(ClassLevelingDefinitionAdapter.class)
	private ClassLevelingDefinition levels;

	@Setting("ExperienceSources")
	private Set<ExperienceSource> experienceSourceSet = new HashSet<>();

	@Setting("Default")
	private boolean defaultClass;

	/*
	@Setting("RequiredClasses")
	@Adapter(ClassConfigAdapter.class)
	*/
	private Set<ClassDefinition> allowedClasses = new HashSet<>();

	public String getName() {
		return name;
	}

	public ItemStack getInfo() {
		return info;
	}

	public void setInfo(ItemStack info) {
		this.info = info;
	}

	public boolean showsInMenu() {
		return showsInMenu;
	}

	public Map<Integer, Float> getPropBonus() {
		return propBonus;
	}

	public void setPropBonus(Map<Integer, Float> propBonus) {
		this.propBonus = propBonus;
	}

	public boolean isShowsInMenu() {
		return showsInMenu;
	}

	public void setShowsInMenu(boolean showsInMenu) {
		this.showsInMenu = showsInMenu;
	}

	public Set<RPGItemType> getAllowedArmor() {
		return allowedArmor;
	}

	public Map<ItemType, Set<ConfigRPGItemType>> getWeapons() {
		return weapons;
	}

	public void addWeapon(ConfigRPGItemType item) {
		Set<ConfigRPGItemType> configRPGItemTypes = weapons.get(item.getRpgItemType().getItemType());
		if (configRPGItemTypes == null) {
			configRPGItemTypes = new HashSet<>();
			weapons.put(item.getRpgItemType().getItemType(), configRPGItemTypes);
		}
		configRPGItemTypes.add(item);
	}

	public HashMap<ItemType, Set<ConfigRPGItemType>> getOffHandWeapons() {
		return offHandWeapons;
	}

	public void addOffHandWeapon(ConfigRPGItemType item) {
		Set<ConfigRPGItemType> configRPGItemTypes = weapons.get(item.getRpgItemType().getItemType());
		if (configRPGItemTypes == null) {
			configRPGItemTypes = new HashSet<>();
			weapons.put(item.getRpgItemType().getItemType(), configRPGItemTypes);
		}
		configRPGItemTypes.add(item);
	}

	public Set<PlayerGroupPermission> getPermissions() {
		return Collections.unmodifiableSet(permissions);
	}

	public void setPermissions(Set<PlayerGroupPermission> permissions) {
		this.permissions = new TreeSet<>(permissions);
	}

	public Map<Integer, Float> getPropLevelBonus() {
		return propLevelBonus;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<ICharacterAttribute, Integer> getStartingAttributes() {
		return startingAttributes;
	}

	public void setStartingAttributes(Map<ICharacterAttribute, Integer> startingAttributes) {
		this.startingAttributes = startingAttributes;
	}

	public String getClassType() {
		return type;
	}

	public Map<IGlobalEffect, EffectParams> getEffects() {
		return effects;
	}

	public void setEffects(Map<IGlobalEffect, EffectParams> effects) {
		this.effects = effects;
	}

	public Map<EntityType, Double> getProjectileDamage() {
		return projectileDamage;
	}

	public List<String> getExitCommands() {
		return exitCommands;
	}

	public void setExitCommands(List<String> exitCommands) {
		this.exitCommands = exitCommands;
	}

	public List<String> getEnterCommands() {
		return enterCommands;
	}

	public void setEnterCommands(List<String> enterCommands) {
		this.enterCommands = enterCommands;
	}

	public TextColor getPreferedColor() {
		return preferedColor;
	}

	public void setPreferedColor(TextColor preferedColor) {
		this.preferedColor = preferedColor;
	}

	public double getExperiencesBonus(String dimmension, EntityType type) {
		Map<EntityType, Double> entityTypeDoubleMap = getExperiences().get(dimmension);
		if (entityTypeDoubleMap == null) {
			return 0;
		}
		Double aDouble = entityTypeDoubleMap.get(type);
		return aDouble == null ? 0 : aDouble;
	}

	public Map<String, Map<EntityType, Double>> getExperiences() {
		return experiences;
	}

	public void setExperiences(Map<String, Map<EntityType, Double>> experiences) {
		this.experiences = experiences;
	}

	@Override
	public String toString() {
		return "PlayerGroup{" +
				"name='" + name + '\'' +
				", getClasses=" + type +
				'}';
	}
}
