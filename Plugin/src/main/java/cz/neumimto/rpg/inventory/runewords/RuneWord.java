package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.players.groups.ConfigClass;
import org.spongepowered.api.item.ItemType;

import java.util.*;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class RuneWord {

    private String name;
    private List<Rune> runes = new ArrayList<>();
    private int minLevel;
    private Set<ConfigClass> restrictedClasses = new HashSet<>();
    private Set<ConfigClass> allowedClasses = new HashSet<>();
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

    public Set<ConfigClass> getRestrictedClasses() {
        return restrictedClasses;
    }

    public void setRestrictedClasses(Set<ConfigClass> restrictedClasses) {
        this.restrictedClasses = restrictedClasses;
    }

    public Set<ConfigClass> getAllowedClasses() {
        return allowedClasses;
    }

    public void setAllowedClasses(Set<ConfigClass> allowedClasses) {
        this.allowedClasses = allowedClasses;
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
