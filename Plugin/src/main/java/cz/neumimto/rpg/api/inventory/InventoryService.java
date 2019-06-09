package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import java.nio.file.Path;
import java.util.Set;

public interface InventoryService<T extends IActiveCharacter> {

    void loadItemGroups(Path path);

    void initializeManagedSlots(T activeCharacter);

    boolean isManagedInventory(Class aClass, int slotId);

    Set<ActiveSkillPreProcessorWrapper> processItemCost(T character, PlayerSkillContext info);

    void initializeCharacterInventory(T character);
}
