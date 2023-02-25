package cz.neumimto.rpg.spigot.bridges.itemsadder;

import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.spigot.items.NamespacedItemDatabase;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemsAdderDatabase implements NamespacedItemDatabase {

    @Override
    public ItemStack findById(String id) {
        CustomStack instance = CustomStack.getInstance(id);

        if (instance == null) {
            Log.error("Unable to find ItemsAdder item " + id);
            return new ItemStack(Material.BARRIER);
        }
        return instance.getItemStack();
    }

    @Override
    public Collection<String> getAll() {
        return ItemsAdder.getAllItems().stream()
                .map(a->"itemsadder:"+a.getId())
                .collect(Collectors.toSet());
    }

    @Override
    public ItemStack findById(String key, int itemModel) {
        return findById(key);
    }
}
