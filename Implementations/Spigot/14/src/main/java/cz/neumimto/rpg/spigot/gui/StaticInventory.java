package cz.neumimto.rpg.spigot.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class StaticInventory {

    public ItemStack[] items;

    private StaticInventory(ItemStack[] items) {
        this.items = items;
    }

    public static StaticInventory of(ItemStack[] items) {
        return new StaticInventory(items);
    }

    public void fill(Inventory inventory) {
        for (int i = 0; i < items.length;) {
            //does java automatically unroll loops?
            inventory.setItem(i, items[i++]);
            inventory.setItem(i, (items[i++]));
            inventory.setItem(i, (items[i++]));
            inventory.setItem(i, (items[i++]));
            inventory.setItem(i, (items[i++]));
            inventory.setItem(i, (items[i++]));
            if (i == 54) {
                return;
            }
            inventory.setItem(i, (items[i++]));
            inventory.setItem(i, (items[i++]));
        }
    }
}
