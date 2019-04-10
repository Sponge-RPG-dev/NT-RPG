package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.items.RpgItemStackImpl;
import cz.neumimto.rpg.common.items.TestItemType;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.players.IActiveCharacter;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.Collections;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage;
import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
class InventoryHandlerTest {

    @Inject
    private InventoryHandler inventoryHandler;


    @Test
    void handleCharacterEquipActionPre(@Stage(READY) IActiveCharacter character) {
        boolean mayUse = inventoryHandler.handleCharacterEquipActionPre(character,
                character.getManagedInventory().getManagedSlots().get(1),
                new RpgItemStackImpl(
                        new TestItemType("itemid", null, new WeaponClass("*"), 10, 0),
                        Collections.emptyMap(),
                        Collections.emptyMap(),
                        Collections.emptyMap()
                ));
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