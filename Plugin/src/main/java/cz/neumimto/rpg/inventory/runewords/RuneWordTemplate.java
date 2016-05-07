package cz.neumimto.rpg.inventory.runewords;

import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 13.12.2015.
 */
public final class RuneWordTemplate {

    private String name;
    private int minLevel;
    private List<String> restrictedClasses;
    private List<String> runes;
    private Map<String, Float> effects;
    private List<String> allowedItems;

    protected RuneWordTemplate() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public List<String> getRestrictedClasses() {
        return restrictedClasses;
    }

    public void setRestrictedClasses(List<String> restrictedClasses) {
        this.restrictedClasses = restrictedClasses;
    }

    public List<String> getRunes() {
        return runes;
    }

    public void setRunes(List<String> runes) {
        this.runes = runes;
    }

    public Map<String, Float> getEffects() {
        return effects;
    }

    public void setEffects(Map<String, Float> effects) {
        this.effects = effects;
    }

    public List<String> getAllowedItems() {
        return allowedItems;
    }

    public void setAllowedItems(List<String> allowedItems) {
        this.allowedItems = allowedItems;
    }
}
