package cz.neumimto.rpg.inventory.runewords;

import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 13.12.2015.
 */
public final class RuneWordTemplate {

    private String name;
    private int minLevel;
    private List<String> blockedGroups;
    private List<String> allowedGroups;
    private List<String> requiredGroups;
    private List<String> runes;
    private Map<String, String> effects;
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

    public List<String> getBlockedGroups() {
        return blockedGroups;
    }

    public void setBlockedGroups(List<String> blockedGroups) {
        this.blockedGroups = blockedGroups;
    }

    public List<String> getRunes() {
        return runes;
    }

    public void setRunes(List<String> runes) {
        this.runes = runes;
    }

    public Map<String, String> getEffects() {
        return effects;
    }

    public void setEffects(Map<String, String> effects) {
        this.effects = effects;
    }

    public List<String> getAllowedItems() {
        return allowedItems;
    }

    public void setAllowedItems(List<String> allowedItems) {
        this.allowedItems = allowedItems;
    }

    public List<String> getAllowedGroups() {
        return allowedGroups;
    }

    public void setAllowedGroups(List<String> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }

    public List<String> getRequiredGroups() {
        return requiredGroups;
    }

    public void setRequiredGroups(List<String> requiredGroups) {
        this.requiredGroups = requiredGroups;
    }
}
