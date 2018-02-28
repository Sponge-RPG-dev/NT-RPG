package cz.neumimto.rpg.players;

import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.RPGItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class RPGItemWrapper {

    private Set<ConfigRPGItemType> items = new HashSet<>();

    private double damage;

    public void addItem(ConfigRPGItemType type) {
        switch (PluginConfig.WEAPON_MERGE_STRATEGY) {
            case 2:
                damage = Math.max(damage, type.getDamage());
                items.add(type);
                break;
            case 1:
                items.add(type);
                damage = items.stream().mapToDouble(ConfigRPGItemType::getDamage).sum();
        }
    }

    public void removeItem(ConfigRPGItemType type) {
        if (items.contains(type)) {
            items.remove(type);
            switch (PluginConfig.WEAPON_MERGE_STRATEGY) {
                case 2:
                    damage = items.stream().mapToDouble(ConfigRPGItemType::getDamage)
                            .max().orElse(0D);
                    break;
                case 1:
                    damage = items.stream().mapToDouble(ConfigRPGItemType::getDamage).sum();
                    break;
            }
        }
    }

    public boolean containsItem(ItemStack is) {
        return containsItem(RPGItemType.from(is));
    }

    public boolean containsItem(RPGItemType from) {
        for (ConfigRPGItemType item : items) {
            if (item.getItemType() == from.getItemType()) {
                if (item.getDisplayName() == null && from.getDisplayName() == null) {
                    return true;
                }
                if (item.getDisplayName().equalsIgnoreCase(from.getDisplayName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<ConfigRPGItemType> getItems() {
        return items;
    }

    public double getDamage() {
        return damage;
    }

    public static RPGItemWrapper createFromSet(Set<ConfigRPGItemType> types) {
        RPGItemWrapper wrapper = new RPGItemWrapper();
        wrapper.addItems(types);
        return wrapper;
    }

    public void addItems(Set<ConfigRPGItemType> value) {
        for (ConfigRPGItemType configRPGItemType : value) {
            addItem(configRPGItemType);
        }
    }
}
