package cz.neumimto.rpg;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.events.EventFactoryService;
import cz.neumimto.rpg.common.utils.ActionResult;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.CharactersExtension.Stage;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class AttributeTests {

    @Inject
    private CharacterService characterService;

    @Inject
    private EventFactoryService eventFactoryService;

    @Test
    public void testAttributeAdd(@Stage(READY) ActiveCharacter ActiveCharacter) {
        ActiveCharacter.setAttributePoints(0);
        ActionResult i = characterService.addAttribute(ActiveCharacter, TestDictionary.AGI);
        Integer attributeValue = ActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 99);
        Assertions.assertFalse(i.isOk());
    }

    @Test
    public void testAttributeAdd2(@Stage(READY) ActiveCharacter ActiveCharacter) {
        ActiveCharacter.setAttributePoints(1);
        HashMap<AttributeConfig, Integer> map = new HashMap<>();
        map.put(TestDictionary.AGI, 2);
        ActionResult i = characterService.addAttribute(ActiveCharacter, map);
        Integer attributeValue = ActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 99);
        Assertions.assertFalse(i.isOk());
    }

    @Test
    public void testAttributeAdd_ok(@Stage(READY) ActiveCharacter ActiveCharacter) {
        ActiveCharacter.getCharacterBase().setUuid(UUID.randomUUID());
        characterService.create(ActiveCharacter.getCharacterBase());
        ActiveCharacter.setAttributePoints(2);
        HashMap<AttributeConfig, Integer> map = new HashMap<>();
        map.put(TestDictionary.AGI, 2);
        ActionResult i = characterService.addAttribute(ActiveCharacter, map);

        Integer attributeValue = ActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 101);
        Assertions.assertTrue(i.isOk());
        Assertions.assertEquals(ActiveCharacter.getAttributePoints(), 0);
        Assertions.assertEquals(ActiveCharacter.getCharacterBase().getAttributePointsSpent(), 2);

        characterService.putInSaveQueue(ActiveCharacter.getCharacterBase());
    }

    @Test
    public void testAttributeAddTransient_ok(@Stage(READY) ActiveCharacter ActiveCharacter) {

        Assertions.assertEquals(ActiveCharacter.getProperty(5), 0);
        Assertions.assertEquals(ActiveCharacter.getProperty(6), 0);
        Map<AttributeConfig, Integer> map = new HashMap<>();
        map.put(TestDictionary.STR, 2);
        map.put(TestDictionary.AGI, 3);
        characterService.addTransientAttribtues(ActiveCharacter, map);
        Integer attributeValue = ActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 102);

        attributeValue = ActiveCharacter.getAttributeValue(TestDictionary.STR);
        Assertions.assertEquals(attributeValue, 12);

        Assertions.assertEquals(ActiveCharacter.getProperty(5), 4);
        Assertions.assertEquals(ActiveCharacter.getProperty(6), 3);
    }
}
