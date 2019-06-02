package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

public interface CharacterInventoryInteractionHandler {

    boolean handleCharacterEquipActionPre(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack);

    boolean handleCharacterUnEquipActionPre(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack);

    boolean handleInventoryInitializationPre(IActiveCharacter character);

    void handleCharacterEquipActionPost(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack);

    void handleCharacterUnEquipActionPost(IActiveCharacter character, ManagedSlot managedSlot);

    void handleInventoryInitializationPost(IActiveCharacter character);

    boolean isValidItemForSlot(ManagedSlot futureSlot, RpgItemStack rpgItemStack);
}
