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

import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class PlayerGroup implements IEffectSourceProvider {
	private final String name;
	private String chatPrefix, chatSufix;
	private Map<Integer, Float> propBonus = new HashMap<>();
	private ItemStack info;
	private boolean showsInMenu = true;
	private Set<ItemType> canCraft = new HashSet<>();
	private Set<ItemType> allowedArmor = new HashSet<>();
	private Map<ItemType, Double> weapons = new HashMap<>();
	private Set<PlayerGroupPermission> permissions = new TreeSet<>();
	private Map<Integer, Float> propLevelBonus = new HashMap<>();
	private ItemType itemType;
	private String description;
	private Map<ICharacterAttribute, Integer> startingAttributes = new HashMap<>();
	private Map<IGlobalEffect, String> effects = new HashMap<>();

	protected cz.neumimto.rpg.effects.IEffectSource playerGroupType;
	private Map<EntityType, Double> projectileDamage = new HashMap<>();

	public PlayerGroup(String name) {
		this.name = name;
		if (name.toLowerCase().equalsIgnoreCase("none")) {
			setShowsInMenu(false);
		}
	}

	public String getName() {
		return name;
	}

	public String getChatPrefix() {
		return chatPrefix;
	}

	public void setChatPrefix(String chatPrefix) {
		this.chatPrefix = chatPrefix;
	}

	public String getChatSufix() {
		return chatSufix;
	}

	public void setChatSufix(String chatSufix) {
		this.chatSufix = chatSufix;
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

	public Set<ItemType> getAllowedArmor() {
		return allowedArmor;
	}

	public Map<ItemType, Double> getWeapons() {
		return weapons;
	}

	public Set<PlayerGroupPermission> getPermissions() {
		return Collections.unmodifiableSet(permissions);
	}

	public void setPermissions(Set<PlayerGroupPermission> permissions) {
		this.permissions = permissions;
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

	public Set<ItemType> getCanCraft() {
		return canCraft;
	}

	public IEffectSource getType() {
		return playerGroupType;
	}

	public Map<IGlobalEffect, String> getEffects() {
		return effects;
	}

	public void setEffects(Map<IGlobalEffect, String> effects) {
		this.effects = effects;
	}

	public Map<EntityType, Double> getProjectileDamage() {
		return projectileDamage;
	}
}
