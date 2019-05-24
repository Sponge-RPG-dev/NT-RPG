package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.players.IActiveCharacter;

import java.nio.file.Path;
import java.util.Set;

public interface InventoryService {

    void loadItemGroups(Path path);

    void initializeManagedSlots(IActiveCharacter activeCharacter);

    boolean isManagedInventory(Class aClass, int slotId);

    Set<ActiveSkillPreProcessorWrapper> processItemCost(IActiveCharacter character, PlayerSkillContext info);
}
