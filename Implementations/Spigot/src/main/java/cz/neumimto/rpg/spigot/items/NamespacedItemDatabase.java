package cz.neumimto.rpg.spigot.items;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface NamespacedItemDatabase {

    ItemStack findById(String id);

    Collection<String> getAll();

    ItemStack findById(String key, int itemModel);
}
