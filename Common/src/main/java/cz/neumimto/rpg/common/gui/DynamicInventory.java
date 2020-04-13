package cz.neumimto.rpg.common.gui;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DynamicInventory<T, I> extends StaticInventory {

    public DynamicInventory(T[] items, T replaceToken, T[] actualContent, InventorySlotProcessor<T, I> processor) {
        super(items, processor);
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < super.items.length; i++) {
            if (items[i].equals(replaceToken)) {
                ids.add(i);
            }
        }

        Iterator<Integer> iterator = ids.iterator();

        for (T T : actualContent) {
            if (iterator.hasNext()) {
                Integer toReplace = iterator.next();
                iterator.remove();
                if (super.items.length + 1 == toReplace) {
                    break;
                }
                super.items[toReplace] = T;
            } else {
                break;
            }
        }
    }
}