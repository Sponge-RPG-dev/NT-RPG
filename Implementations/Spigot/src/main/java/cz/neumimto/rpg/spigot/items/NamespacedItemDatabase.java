package cz.neumimto.rpg.spigot.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface NamespacedItemDatabase {

    ItemStack findById(NamespacedKey id);

    Collection<String> getAll();

    ItemStack findById(NamespacedKey key, int itemModel);
}
