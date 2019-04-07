package cz.neumimto.rpg.api.inventory;

import java.util.Map;

public interface RpgInventory {

    Map<Integer, ManagedSlot> getManagedSlots();
}
