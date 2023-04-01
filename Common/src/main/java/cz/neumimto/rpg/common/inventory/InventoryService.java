package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.model.EquipedSlot;

public interface InventoryService<T extends ActiveCharacter> {

    void load();

    void reload();

    EquipedSlot createEquipedSlot(String className, int slotId);

    void invalidateGUICaches(T cc);

}
