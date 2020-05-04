package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.persistance.model.EquipedSlot;

public interface InventoryService<T extends IActiveCharacter> {

    void load();

    void reload();

    void initializeManagedSlots(T activeCharacter);

    boolean isManagedInventory(Class aClass, int slotId);

    void initializeCharacterInventory(T character);

    EquipedSlot createEquipedSlot(String className, int slotId);

    void invalidateGUICaches(IActiveCharacter cc);

}
