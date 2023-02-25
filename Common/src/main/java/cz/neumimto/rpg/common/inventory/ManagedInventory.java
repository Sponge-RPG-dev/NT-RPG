package cz.neumimto.rpg.common.inventory;

import java.util.HashMap;

public class ManagedInventory {

    private final Class<?> type;
    private final HashMap<Integer, FilteredManagedSlotImpl> slots;

    public ManagedInventory(Class<?> type, HashMap<Integer, FilteredManagedSlotImpl> slots) {

        this.type = type;
        this.slots = slots;
    }

    public Class<?> getType() {
        return type;
    }

    public HashMap<Integer, FilteredManagedSlotImpl> getSlots() {
        return slots;
    }
}
