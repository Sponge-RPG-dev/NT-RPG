package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInventoryService implements InventoryService {


    protected Map<Class<?>, ManagedInventory> managedInventories = new HashMap<>();

    @Override
    public void initializeManagedSlots(IActiveCharacter activeCharacter) {
        Map<Class<?>, RpgInventory> managedInventory = activeCharacter.getManagedInventory();
        for (Map.Entry<Class<?>, ManagedInventory> entry : managedInventories.entrySet()) {
            Class<?> key = entry.getKey();
            ManagedInventory mi = entry.getValue();
            RpgInventoryImpl rpgInventory = new RpgInventoryImpl();
            for (SlotEffectSource value : mi.getSlots().values()) {
                rpgInventory.getManagedSlots().put(value.getSlotId(), new ManagedSlotImpl(value.getSlotId()));
            }
            managedInventory.put(key, rpgInventory);
        }
    }

    @Override
    public boolean isManagedInventory(Class aClass, int slotId) {
        ManagedInventory managedInventory = managedInventories.get(aClass);
        return managedInventory != null && managedInventory.getSlots().containsKey(slotId);
    }
}
