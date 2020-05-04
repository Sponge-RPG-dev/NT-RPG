package cz.neumimto.rpg.common.gui;

public abstract class ConfigInventory<T, I> {

    protected InventorySlotProcessor<T, I> processor;
    protected T[] items;

    public ConfigInventory(T[] items, InventorySlotProcessor<T, I> processor) {
        this.items = items;
        this.processor = processor;
    }

    public abstract void fill(I inventory);

}