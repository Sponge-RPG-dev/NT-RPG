package cz.neumimto.rpg.players;

import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.RPGItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class RPGItemWrapper {

    private Set<ConfigRPGItemType> items = new HashSet<>();

    private double maxDamage;

    public void addItem(ConfigRPGItemType type) {
        maxDamage = Math.max(maxDamage, type.getDamage());
        items.add(type);
    }

    public void removeItem(ConfigRPGItemType type) {
        if (items.contains(type))
            items.remove(type);
        maxDamage = items.stream().mapToDouble(ConfigRPGItemType::getDamage)
                .max().orElse(0D);
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

    public double getMaxDamage() {
        return maxDamage;
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
