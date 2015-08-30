package cz.neumimto.inventory;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.type.Inventory2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 22.7.2015.
 */
public class InventoryMenu {
    private List<InventoryMenuConsumer> consumers = new ArrayList<>();
    private List<ItemStack> itemStacks = new ArrayList<>();
    private List<ItemIndex> itemloc = new ArrayList<>();
    private Inventory2D inventory;

    public InventoryMenu(Inventory2D inventory) {
        this.inventory = inventory;
    }

    public InventoryMenu next(int row, int column, ItemStack itemStack, InventoryMenuConsumer consumer) {
        itemStacks.add(itemStack);
        consumers.add(consumer);
        ItemIndex index = new ItemIndex();
        index.x = row;
        index.y = column;
        index.index = itemloc.size();
        itemloc.add(index);
        inventory.set(new SlotPos(row,column), itemStack);
        return this;
    }

    private class ItemIndex {
        int x, y, index;
    }

    public InventoryMenuConsumer getConsumer(int x, int y) {
        for (ItemIndex index : itemloc) {
            if (index.x == x && index.y == y) {
                if (consumers.size() <= index.index) {
                    return consumers.get(index.index);
                }
            }
        }
        return InventoryMenuConsumer.EMPTY;
    }

}
