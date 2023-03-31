package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.model.EquipedSlot;

public interface InventoryService<T extends IActiveCharacter> {

    void load();

    void reload();

    EquipedSlot createEquipedSlot(String className, int slotId);

    void invalidateGUICaches(T cc);

}
