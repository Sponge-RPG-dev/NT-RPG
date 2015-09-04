package cz.neumimto.inventory;

import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 22.7.2015.
 */
public class Option {
    private int row, column;
    private ItemStack itemStack;
    private InventoryMenuConsumer consumer;

    public Option(int row, int column, ItemStack itemStack, InventoryMenuConsumer consumer) {
        this.row = row;
        this.column = column;
        this.itemStack = itemStack;
        this.consumer = consumer;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public InventoryMenuConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(InventoryMenuConsumer consumer) {
        this.consumer = consumer;
    }
}
