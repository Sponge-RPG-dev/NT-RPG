package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.persistance.model.EquipedSlot;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;

import java.util.Set;

public interface InventoryService<T extends IActiveCharacter> {

    void load();

    void initializeManagedSlots(T activeCharacter);

    boolean isManagedInventory(Class aClass, int slotId);

    Set<ActiveSkillPreProcessorWrapper> processItemCost(T character, PlayerSkillContext info);

    void initializeCharacterInventory(T character);

    EquipedSlot createEquipedSlot(String className, int slotId);

    String getItemIconForSkill(ISkill iSkill);
}
