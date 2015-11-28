package cz.neumimto.effects.runewords;

import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.players.groups.NClass;
import org.spongepowered.api.item.ItemType;

import javax.naming.ldap.HasControls;
import java.util.*;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class RuneWord {

    private String name;
    private List<Rune> runes = new ArrayList<>();
    private int minLevel;
    private Set<NClass> restrictedClasses = new HashSet<>();
    private Map<IGlobalEffect,Double> effects = new HashMap<>();
    private Set<ItemType> requiredItemType = new HashSet<>();
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

    public Set<NClass> getRestrictedClasses() {
        return restrictedClasses;
    }

    public void setRestrictedClasses(Set<NClass> restrictedClasses) {
        this.restrictedClasses = restrictedClasses;
    }

    public Map<IGlobalEffect, Double> getEffects() {
        return effects;
    }

    public void setEffects(Map<IGlobalEffect, Double> effects) {
        this.effects = effects;
    }
}
