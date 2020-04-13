package cz.neumimto.rpg.common.gui;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DynamicInventory<T, I> extends StaticInventory<T, I> {

    protected final T replaceToken;
    protected List<Integer> ids = new ArrayList<>();

    public DynamicInventory(T[] items, T replaceToken, InventorySlotProcessor<T, I> processor) {
        super(items, processor);
        this.replaceToken = replaceToken;
        for (int i = 0; i < super.items.length; i++) {
            if (items[i].equals(replaceToken)) {
                ids.add(i);
            }
        }
    }

    public DynamicInventory setActualContent(T[] actualContent) {
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
        return this;
    }
}