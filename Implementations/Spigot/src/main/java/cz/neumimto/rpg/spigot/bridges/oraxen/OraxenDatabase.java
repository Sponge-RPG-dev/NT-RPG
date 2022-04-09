package cz.neumimto.rpg.spigot.bridges.oraxen;

import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.spigot.items.NamespacedItemDatabase;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OraxenDatabase implements NamespacedItemDatabase {

    // oraxen:my_item
    public ItemStack findById(String id) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(id);
        id = namespacedKey.value();
        if (!OraxenItems.exists(id)) {
            Log.error("Unable to find Oraxen item " + id);
            return new ItemStack(Material.BARRIER);
        }
        return OraxenItems.getItemById(id).build();
    }

    @Override
    public ItemStack findById(NamespacedKey id) {
        return findById(id.value());
    }

    @Override
    public ItemStack findById(NamespacedKey key, int itemModel) {
        return findById(key);
    }

    @Override
    public Collection<String> getAll() {
        return Stream.of(OraxenItems.getItemNames())
                .map(a->"oraxen:"+a)
                .collect(Collectors.toSet());
    }
}
