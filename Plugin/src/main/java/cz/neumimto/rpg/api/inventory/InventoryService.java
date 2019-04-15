package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.players.IActiveCharacter;

import java.nio.file.Path;

public interface InventoryService {

    void loadItemGroups(Path path);

    void initializeManagedSlots(IActiveCharacter activeCharacter);
}
