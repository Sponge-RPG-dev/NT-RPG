package cz.neumimto.rpg;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.CharactersExtension.Stage;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class AttributeTests {

    @Inject
    private ICharacterService<? super IActiveCharacter> characterService;

    @Inject
    private EventFactoryService eventFactoryService;

    @BeforeEach
    public void before() {
        NtRpgPlugin.GlobalScope.eventFactory = eventFactoryService;
        new TestDictionary().reset();
    }

    @Test
    public void testAttributeAdd(@Stage(READY)IActiveCharacter iActiveCharacter) {
        iActiveCharacter.setAttributePoints(0);
        int i = characterService.addAttribute(iActiveCharacter, TestDictionary.AGI);
        Integer attributeValue = iActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 99);
        Assertions.assertEquals(i, 1);
    }

    @Test
    public void testAttributeAdd2(@Stage(READY)IActiveCharacter iActiveCharacter) {
        iActiveCharacter.setAttributePoints(1);
        int i = characterService.addAttribute(iActiveCharacter, TestDictionary.AGI, 2);
        Integer attributeValue = iActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 99);
        Assertions.assertEquals(i, 1);
    }

    @Test
    public void testAttributeAdd_ok(@Stage(READY)IActiveCharacter iActiveCharacter) {
        iActiveCharacter.setAttributePoints(2);
        int i = characterService.addAttribute(iActiveCharacter, TestDictionary.AGI, 2);
        Integer attributeValue = iActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 101);
        Assertions.assertEquals(i, 0);
        Assertions.assertEquals(iActiveCharacter.getAttributePoints(),0);
        Assertions.assertEquals(iActiveCharacter.getCharacterBase().getAttributePointsSpent(),2);
        Assertions.assertTrue(iActiveCharacter.requiresDamageRecalculation());

    }

    @Test
    public void testAttributeAddTransient_ok(@Stage(READY)IActiveCharacter iActiveCharacter) {

        Assertions.assertEquals(iActiveCharacter.getProperty(5), 0);
        Assertions.assertEquals(iActiveCharacter.getProperty(6), 0);
        Map<AttributeConfig, Integer> map = new HashMap<>();
        map.put(TestDictionary.STR, 2);
        map.put(TestDictionary.AGI, 3);
        characterService.addTransientAttribtues(iActiveCharacter, map);
        Integer attributeValue = iActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 102);

        attributeValue = iActiveCharacter.getAttributeValue(TestDictionary.STR);
        Assertions.assertEquals(attributeValue, 12);

        Assertions.assertEquals(iActiveCharacter.getProperty(5), 4);
        Assertions.assertEquals(iActiveCharacter.getProperty(6), 3);
    }
}
