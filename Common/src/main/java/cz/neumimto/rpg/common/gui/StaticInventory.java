package cz.neumimto.rpg.common.gui;


public class StaticInventory<T, I> extends ConfigInventory<T, I> {

    public StaticInventory(T[] items, InventorySlotProcessor<T, I> processor) {
        super(items, processor);
    }


    @Override
    public void fill(I inventory) {
        for (int i = 0; i < 54; i++) {
            processor.setItem(items[i], inventory, i);
        }
    }
}