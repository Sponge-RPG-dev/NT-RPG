package cz.neumimto.rpg.spigot.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ConfigInventory {

    public ItemStack[] items;

    private ConfigInventory(ItemStack[] items) {
        this.items = items;
    }

    public static ConfigInventory of(ItemStack[] items) {
        return new StaticInventory(items);
    }

    public static ConfigInventory of(ItemStack[] staticPart, ItemStack replaceToken, ItemStack[] arr) {
        return new DynamicInventory(staticPart, replaceToken, arr);
    }

    public abstract void fill(Inventory inventory);

    private static class StaticInventory extends ConfigInventory {
        private StaticInventory(ItemStack[] items) {
            super(items);
        }

        @Override
        public void fill(Inventory inventory) {
            for (int i = 0; i < items.length; ) {
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

    //todo paging
    private static class DynamicInventory extends StaticInventory {

        private DynamicInventory(ItemStack[] items, ItemStack replaceToken, ItemStack[] actualContent) {
            super(items);
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < super.items.length; i++) {
                if (items[i].equals(replaceToken)) {
                    ids.add(i);
                }
            }

            Iterator<Integer> iterator = ids.iterator();

            for (ItemStack itemStack : actualContent) {
                if (iterator.hasNext()) {
                    Integer toReplace = iterator.next();
                    iterator.remove();
                    if (super.items.length + 1 == toReplace) {
                        break;
                    }
                    super.items[toReplace] = itemStack;
                } else {
                    break;
                }
            }
        }
    }
}
