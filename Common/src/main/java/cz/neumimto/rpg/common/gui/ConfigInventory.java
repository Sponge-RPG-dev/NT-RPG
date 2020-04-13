package cz.neumimto.rpg.common.gui;

public abstract class ConfigInventory<T, I> {

    protected InventorySlotProcessor<T, I> processor;
    protected T[] items;
    private boolean preInitialize;
    public ConfigInventory(T[] items, InventorySlotProcessor<T, I> processor) {
        this.items = items;
        this.processor = processor;
    }

    public abstract void fill(I inventory);

    public boolean isPreInitialize() {
        return preInitialize;
    }

    public void setPreInitialize(boolean preInitialize) {
        this.preInitialize = preInitialize;
    }
}