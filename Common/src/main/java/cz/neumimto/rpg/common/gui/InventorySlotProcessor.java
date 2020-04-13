package cz.neumimto.rpg.common.gui;

@FunctionalInterface
public interface InventorySlotProcessor<T, I> {

    void setItem(T item, I inventory, int slotId);
}
