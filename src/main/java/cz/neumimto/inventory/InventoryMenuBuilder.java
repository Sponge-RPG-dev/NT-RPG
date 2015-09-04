package cz.neumimto.inventory;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 22.7.2015.
 */
public class InventoryMenuBuilder {

    private Set<Option> options = new HashSet<>();

    public static InventoryMenuBuilder create() {
        return new InventoryMenuBuilder();
    }

    public InventoryMenuBuilder addOption(int row, int column, ItemStack i, InventoryMenuConsumer consumer) {
        if (i == null)
            throw new IllegalArgumentException("itemStack cannot be null");
        if (consumer == null)
            throw new IllegalArgumentException("consumer cannot be null");
        options.add(new Option(row, column, i, consumer));
        return this;
    }

    public InventoryMenu build() {
        InventoryMenu inventoryMenu = new InventoryMenu(null);
        for (Option o : options) {
            inventoryMenu.next(o.getRow(), o.getColumn(), o.getItemStack(), o.getConsumer());
        }
        return inventoryMenu;
    }
}
