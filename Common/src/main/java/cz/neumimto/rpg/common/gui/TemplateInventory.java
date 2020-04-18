package cz.neumimto.rpg.common.gui;

public class TemplateInventory<T, I> extends DynamicInventory<T, I> {

    public TemplateInventory(T[] items, T replaceToken, InventorySlotProcessor<T, I> processor) {
        super(items, replaceToken, processor);
    }

    public DynamicInventory setActualContent(T[] actualContent) {
        Object[] items = new Object[super.items.length];
        for (int i = 0; i < super.items.length; i++) {
            items[i] = super.items[i];
        }

        DynamicInventory di = new DynamicInventory(items, replaceToken, processor);
        di.setActualContent(actualContent);
        return di;
    }
}
