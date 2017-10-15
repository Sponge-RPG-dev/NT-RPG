package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import org.spongepowered.api.item.ItemType;

import java.util.*;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class RuneWord {

	private String name;
	private List<Rune> runes = new ArrayList<>();
	private int minLevel;
	private Set<PlayerGroup> allowedGroups = new HashSet<>();

	private Map<IGlobalEffect, String> effects = new HashMap<>();
	private Set<ItemType> allowedItems = new HashSet<>();

	private String lore;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Rune> getRunes() {
		return runes;
	}

	public void setRunes(List<Rune> runes) {
		this.runes = runes;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public Set<PlayerGroup> getAllowedGroups() {
		return allowedGroups;
	}

	public void setAllowedGroups(Set<PlayerGroup> allowedGroups) {
		this.allowedGroups = allowedGroups;
	}

	public Map<IGlobalEffect, String> getEffects() {
		return effects;
	}

	public void setEffects(Map<IGlobalEffect, String> effects) {
		this.effects = effects;
	}

	public Set<ItemType> getAllowedItems() {
		return allowedItems;
	}

	public void setAllowedItems(Set<ItemType> allowedItems) {
		this.allowedItems = allowedItems;
	}

	public String getLore() {
		return lore;
	}

	public void setLore(String lore) {
		this.lore = lore;
	}
}
