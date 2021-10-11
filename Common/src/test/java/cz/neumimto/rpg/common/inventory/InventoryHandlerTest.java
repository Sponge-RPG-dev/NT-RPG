package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.RpgTest;
import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.items.RpgItemStackImpl;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.junit.TestGuiceModule;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage;
import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
class InventoryHandlerTest {

    @Inject
    private InventoryHandler inventoryHandler;

    @Inject
    private ItemService itemService;

    @Inject
    private PropertyService propertyService;


    private TestCharacter character;

    @Inject
    private RpgApi api;


    @BeforeEach
    public void beforeEach(@Stage(READY) IActiveCharacter character) {
        this.character = (TestCharacter) character;
        new RpgTest(api);
        if (propertyService.getAttributes().isEmpty()) {
            propertyService.getAttributes().put(TestDictionary.STR.getId(), TestDictionary.STR);
            propertyService.getAttributes().put(TestDictionary.AGI.getId(), TestDictionary.AGI);
        }
    }

    @ParameterizedTest
    @MethodSource("methodProvider01")
    void handleCharacterEquipActionPreTest(RpgItemStack testItemStack, int i, boolean result) {

        boolean mayUse = inventoryHandler.handleCharacterEquipActionPre(character,
                character.getManagedInventory().get(Object.class).getManagedSlots().get(i),
                testItemStack);
        Log.info("Id: " + i + " result: " + result);
        Assertions.assertSame(mayUse, result);
    }


    private static Stream<Arguments> methodProvider01() {

        return Stream.of(
                //May use - allowed weapon, slot matches
                Arguments.of(new RpgItemStackImpl(TestDictionary.ITEM_TYPE_WEAPON_1, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()), 1, true),

                //May use - allowed armor, slot matches
                Arguments.of(new RpgItemStackImpl(TestDictionary.ARMOR_TYPE_1, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()), 0, true),

                //Maynot use - item in allowed but checked against a in wrong slot.
                Arguments.of(new RpgItemStackImpl(TestDictionary.ARMOR_TYPE_1, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()), 1, false),

                //May not use - The slot wont accept weapon class
                Arguments.of(new RpgItemStackImpl(TestDictionary.ITEM_TYPE_WEAPON_2, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()), 0, false),

                //May not use - The slot accepts weapon class, but character cannot use such item type
                Arguments.of(new RpgItemStackImpl(TestDictionary.ITEM_TYPE_WEAPON_2, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()), 1, false),

                //May not use - allowed weapon, slot matches, not enough str
                Arguments.of(new RpgItemStackImpl(TestDictionary.ITEM_TYPE_WEAPON_1, Collections.emptyMap(), new HashMap<AttributeConfig, Integer>() {{
                    put(TestDictionary.STR, 0);
                    put(TestDictionary.AGI, 0);
                }}, new HashMap<AttributeConfig, Integer>() {{
                    put(TestDictionary.STR, 60);
                }}, Collections.emptyMap(), Collections.emptyMap()), 1, false),

                //May use - allowed weapon, slot matches, enough str and agi
                Arguments.of(new RpgItemStackImpl(TestDictionary.ITEM_TYPE_WEAPON_1, Collections.emptyMap(), new HashMap<AttributeConfig, Integer>() {{
                    put(TestDictionary.STR, 5);
                    put(TestDictionary.AGI, 5);
                }}, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()), 1, true),

                //May not use - allowed weapon, slot matches, enough str and agi, not enough class level
                Arguments.of(new RpgItemStackImpl(TestDictionary.ITEM_TYPE_WEAPON_1, Collections.emptyMap(),
                        new HashMap<AttributeConfig, Integer>() {{
                            put(TestDictionary.STR, 5);
                            put(TestDictionary.AGI, 5);
                        }},
                        Collections.emptyMap(),
                        new HashMap<ClassDefinition, Integer>() {{
                            put(TestDictionary.CLASS_PRIMARY, 10);
                        }}, Collections.emptyMap()), 1, false),

                //May use - allowed weapon, slot matches, enough str and agi, enough class and level
                Arguments.of(new RpgItemStackImpl(TestDictionary.ITEM_TYPE_WEAPON_1, Collections.emptyMap(),
                        new HashMap<AttributeConfig, Integer>() {{
                            put(TestDictionary.STR, 5);
                            put(TestDictionary.AGI, 5);
                        }},
                        Collections.emptyMap(),
                        new HashMap<ClassDefinition, Integer>() {{
                            put(TestDictionary.CLASS_PRIMARY, 1);
                        }}, Collections.emptyMap()), 1, true),

                //May not use - allowed weapon, slot matches, enough str and agi, no specified class
                Arguments.of(new RpgItemStackImpl(TestDictionary.ITEM_TYPE_WEAPON_1, Collections.emptyMap(),
                        new HashMap<AttributeConfig, Integer>() {{
                            put(TestDictionary.STR, 5);
                            put(TestDictionary.AGI, 5);
                        }},
                        Collections.emptyMap(),
                        new HashMap<ClassDefinition, Integer>() {{
                            put(TestDictionary.CLASS_TERTIARY, 5);
                        }}, Collections.emptyMap()), 1, false)
        );

    }


    @Test
    void characterAttemptsToRemoveItemWithAttributes01() {
        Map<Integer, ManagedSlot> managedSlots = character.getManagedInventory().get(Object.class).getManagedSlots();

        ManagedSlotImpl managedSlot = new ManagedSlotImpl(0);
        Map<AttributeConfig, Integer> minimalRequirements = new HashMap<>();
        minimalRequirements.put(TestDictionary.STR, 2);

        RpgItemStackImpl rpgItemStack = new RpgItemStackImpl(TestDictionary.ARMOR_TYPE_1, Collections.emptyMap(), Collections.emptyMap(), minimalRequirements, Collections.emptyMap(), Collections.emptyMap());
        managedSlot.setContent(rpgItemStack);
        managedSlots.put(0, managedSlot);

        minimalRequirements = new HashMap<>();
        minimalRequirements.put(TestDictionary.STR, 3);
        RpgItemStackImpl future = new RpgItemStackImpl(TestDictionary.ARMOR_TYPE_1, Collections.emptyMap(), new HashMap<AttributeConfig, Integer>() {{
            put(TestDictionary.STR, 0);
        }},
                minimalRequirements, Collections.emptyMap(), Collections.emptyMap());

        boolean mayUse = itemService.checkItemAttributeRequirements(character, future);
        Assertions.assertTrue(mayUse);
    }

    @Test
    void characterAttemptsToRemoveItemWithAttributes02() {
        Map<Integer, ManagedSlot> managedSlots = character.getManagedInventory().get(Object.class).getManagedSlots();

        ManagedSlotImpl managedSlot = new ManagedSlotImpl(0);
        Map<AttributeConfig, Integer> minimalRequirements = new HashMap<>();
        minimalRequirements.put(TestDictionary.STR, 2);

        RpgItemStackImpl rpgItemStack = new RpgItemStackImpl(TestDictionary.ARMOR_TYPE_1, Collections.emptyMap(), Collections.emptyMap(),
                minimalRequirements, Collections.emptyMap(), Collections.emptyMap());
        managedSlot.setContent(rpgItemStack);
        managedSlots.put(0, managedSlot);

        minimalRequirements = new HashMap<>();
        minimalRequirements.put(TestDictionary.STR, 11); //character has 10 str
        minimalRequirements.put(TestDictionary.AGI, 0);
        RpgItemStackImpl future = new RpgItemStackImpl(TestDictionary.ARMOR_TYPE_1, Collections.emptyMap(),
                new HashMap<AttributeConfig, Integer>() {{
                    put(TestDictionary.STR, 0);
                    put(TestDictionary.AGI, 0);
                }},
                minimalRequirements, Collections.emptyMap(), Collections.emptyMap());

        boolean mayUse = itemService.checkItemAttributeRequirements(character, future);
        Assertions.assertFalse(mayUse);
    }

    @Test
    void characterAttemptsToRemoveItemWithAttributes03() {
        Map<Integer, ManagedSlot> managedSlots = character.getManagedInventory().get(Object.class).getManagedSlots();

        ManagedSlotImpl managedSlot = new ManagedSlotImpl(0);
        Map<AttributeConfig, Integer> minimalRequirements = new HashMap<>();
        minimalRequirements.put(TestDictionary.STR, 2);

        RpgItemStackImpl rpgItemStack = new RpgItemStackImpl(TestDictionary.ARMOR_TYPE_1, Collections.emptyMap(), Collections.emptyMap(), minimalRequirements, Collections.emptyMap(), Collections.emptyMap());
        managedSlot.setContent(rpgItemStack);
        managedSlots.put(0, managedSlot);

        minimalRequirements = new HashMap<>();
        minimalRequirements.put(TestDictionary.STR, 10); //character has 10 str
        minimalRequirements.put(TestDictionary.AGI, 0);

        Map<AttributeConfig, Integer> bonusAttributes = new HashMap<>();
        bonusAttributes.put(TestDictionary.STR, 5);
        RpgItemStackImpl future = new RpgItemStackImpl(TestDictionary.ARMOR_TYPE_1, Collections.emptyMap(), bonusAttributes, minimalRequirements, Collections.emptyMap(), Collections.emptyMap());

        boolean mayUse = itemService.checkItemAttributeRequirements(character, future);
        Assertions.assertFalse(mayUse);
    }
}