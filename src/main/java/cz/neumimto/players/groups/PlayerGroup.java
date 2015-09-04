package cz.neumimto.players.groups;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class PlayerGroup {
    private final String name;
    private String chatPrefix, chatSufix;
    private Map<Integer, Float> propBonus = new HashMap<>();
    private ItemStack info;
    private boolean showsInMenu = true;
    private Set<ItemType> canCraft = new HashSet<>();
    private Set<ItemType> allowedArmor = new HashSet<>();
    private Map<ItemType, Double> weapons = new HashMap<>();
    private Set<String> permissions = Collections.synchronizedSet(new HashSet<>());
    private Map<Integer, Float> propLevelBonus = new HashMap<>();

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

    public void setShowsInMenu(boolean showsInMenu) {
        this.showsInMenu = showsInMenu;
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

    public Set<ItemType> getAllowedArmor() {
        return allowedArmor;
    }

    public Map<ItemType, Double> getWeapons() {
        return weapons;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Map<Integer, Float> getPropLevelBonus() {
        return propLevelBonus;
    }
}
