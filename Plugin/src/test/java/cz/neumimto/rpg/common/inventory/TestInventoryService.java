package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.persistance.model.EquipedSlot;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.common.entity.TestCharacter;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

@Singleton
public class TestInventoryService extends AbstractInventoryService<TestCharacter> {

    @Override
    public void loadItemGroups(Path path) {

    }

    @Override
    public Set<ActiveSkillPreProcessorWrapper> processItemCost(TestCharacter character, PlayerSkillContext info) {
        return Collections.emptySet();
    }

    @Override
    public void initializeCharacterInventory(TestCharacter character) {

    }

    @Override
    public EquipedSlot createEquipedSlot(String className, int slotId) {
        return new EquipedSlot() {
            @Override
            public String getClassName() {
                return Object.class.getName();
            }

            @Override
            public int getSlotIndex() {
                return slotId;
            }
        };
    }
}
