package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.model.EquipedSlot;

import javax.inject.Singleton;

@Singleton
public class TestInventoryService extends AbstractInventoryService<TestCharacter> {


    @Override
    public void load() {

    }

    @Override
    public EquipedSlot createEquipedSlot(String className, int slotId) {
        return () -> slotId;
    }

    @Override
    public void invalidateGUICaches(TestCharacter cc) {

    }


}
