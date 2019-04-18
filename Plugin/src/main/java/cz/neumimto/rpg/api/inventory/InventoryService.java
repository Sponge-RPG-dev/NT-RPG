package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.inventory.Container;

import java.nio.file.Path;

public interface InventoryService {

    void loadItemGroups(Path path);

    void initializeManagedSlots(IActiveCharacter activeCharacter);

    boolean isManagedInventory(Class<? extends Container> aClass, int slotId);
}
