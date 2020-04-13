package cz.neumimto.rpg.common.gui;

public class StaticInventory<T, I> extends ConfigInventory<T, I> {

    public StaticInventory(T[] items, InventorySlotProcessor<T, I> processor) {
        super(items, processor);
    }


    @Override
    public void fill(T inventory) {
        for (int i = 0; i < items.length; ) {
            //does java automatically unroll loops?

            processor.setItem(items[i], inventory, i);
            i++;
            processor.setItem(items[i], inventory, i);
            i++;
            processor.setItem(items[i], inventory, i);
            i++;
            processor.setItem(items[i], inventory, i);
            i++;
            processor.setItem(items[i], inventory, i);
            i++;
            processor.setItem(items[i], inventory, i);
            i++;
            processor.setItem(items[i], inventory, i);
            i++;
            if (i == 54) {
                return;
            }
            processor.setItem(items[i], inventory, i);
            i++;
            processor.setItem(items[i], inventory, i);
            i++;
        }
    }
}