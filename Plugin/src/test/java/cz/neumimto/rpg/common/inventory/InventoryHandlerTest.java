package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.PreparedCharactersExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.players.CharacterService;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, PreparedCharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
class InventoryHandlerTest {

    @Inject
    private InventoryHandler inventoryHandler;

    @Inject
    private CharacterService characterService;

    @Test
    void handleCharacterEquipActionPre() {
    }

    @Test
    void handleCharacterUnEquipActionPre() {
    }

    @Test
    void handleInventoryInitializationPre() {
    }

    @Test
    void handleCharacterEquipActionPost() {
    }

    @Test
    void handleCharacterUnEquipActionPost() {
    }

    @Test
    void handleInventoryInitializationPost() {
    }

    @Test
    void isValidItemForSlot() {
    }
}