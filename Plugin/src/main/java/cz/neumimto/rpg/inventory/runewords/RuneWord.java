package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.players.groups.ConfigClass;
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
    private Set<PlayerGroup> blockedGroups = new HashSet<>();
    private Set<PlayerGroup> allowedGroups = new HashSet<>();
    private Set<PlayerGroup> requiredGroups = new HashSet<>();

    private Map<IGlobalEffect, Float> effects = new HashMap<>();
    private Set<ItemType> allowedItems = new HashSet<>();

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

    public Set<PlayerGroup> getBlockedGroups() {
        return blockedGroups;
    }

    public void setBlockedGroups(Set<PlayerGroup> blockedGroups) {
        this.blockedGroups = blockedGroups;
    }

    public Set<PlayerGroup> getAllowedGroups() {
        return allowedGroups;
    }

    public void setAllowedGroups(Set<PlayerGroup> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }

    public Set<PlayerGroup> getRequiredGroups() {
        return requiredGroups;
    }

    public void setRequiredGroups(Set<PlayerGroup> requiredGroups) {
        this.requiredGroups = requiredGroups;
    }

    public Map<IGlobalEffect, Float> getEffects() {
        return effects;
    }

    public void setEffects(Map<IGlobalEffect, Float> effects) {
        this.effects = effects;
    }

    public Set<ItemType> getAllowedItems() {
        return allowedItems;
    }

    public void setAllowedItems(Set<ItemType> allowedItems) {
        this.allowedItems = allowedItems;
    }
}
