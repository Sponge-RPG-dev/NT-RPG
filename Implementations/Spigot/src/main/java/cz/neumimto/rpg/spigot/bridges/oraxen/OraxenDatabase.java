package cz.neumimto.rpg.spigot.bridges.oraxen;

import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.spigot.items.NamespacedItemDatabase;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OraxenDatabase implements NamespacedItemDatabase {

    // oraxen:my_item
    @Override
    public ItemStack findById(String id) {
        if (!OraxenItems.exists(id)) {
            Log.error("Unable to find Oraxen item " + id);
            return new ItemStack(Material.BARRIER);
        }
        return OraxenItems.getItemById(id).build();
    }

    @Override
    public ItemStack findById(String key, int itemModel) {
        return findById(key);
    }

    @Override
    public Collection<String> getAll() {
        return Stream.of(OraxenItems.getItemNames())
                .map(a->"oraxen:"+a)
                .collect(Collectors.toSet());
    }
}
