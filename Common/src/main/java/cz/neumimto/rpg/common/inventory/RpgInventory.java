package cz.neumimto.rpg.common.inventory;

import java.util.Map;

public interface RpgInventory {

    Map<Integer, ManagedSlot> getManagedSlots();

}
