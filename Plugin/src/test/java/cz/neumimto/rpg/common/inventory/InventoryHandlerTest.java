package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.items.RpgItemStackImpl;
import cz.neumimto.rpg.common.items.TestItemType;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.players.IActiveCharacter;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.util.Collections;
import java.util.stream.Stream;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage;
import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
class InventoryHandlerTest {

    @Inject
    private InventoryHandler inventoryHandler;

    private IActiveCharacter character;

    @BeforeEach
    public void beforeEach(@Stage(READY) IActiveCharacter character) {
        this.character = character;
    }

    @ParameterizedTest
    @MethodSource("methodProvider01")
    void handleCharacterEquipActionPre(RpgItemStack testItemStack, int i, boolean result) {
        boolean mayUse = inventoryHandler.handleCharacterEquipActionPre(character,
                character.getManagedInventory().getManagedSlots().get(i),
                testItemStack);

        Assertions.assertSame(mayUse, result);
    }



    private static Stream<Arguments> methodProvider01() {

        return Stream.of(
                Arguments.of(new RpgItemStackImpl(new TestItemType("itemid", null, new WeaponClass("*"), 10, 0),
                                    Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()),1, true) ,
                Arguments.of(new RpgItemStackImpl(new TestItemType("itemid", null, new WeaponClass("1"), 10, 0),
                                    Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()),0, false)
        );

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