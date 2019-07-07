package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.inventory.RpgInventory;

import java.util.HashMap;
import java.util.Map;

public class RpgInventoryImpl implements RpgInventory {

    private Map<Integer, ManagedSlot> managedSlots = new HashMap<>();

    @Override
    public Map<Integer, ManagedSlot> getManagedSlots() {
        return managedSlots;
    }


}
