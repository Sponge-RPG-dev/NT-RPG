package cz.neumimto.rpg;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.utils.ActionResult;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.CharactersExtension.Stage;
import cz.neumimto.rpg.junit.H2TestGuiceModule;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(H2TestGuiceModule.class)
public class AttributeTests {

    @Inject
    private ICharacterService characterService;

    @Inject
    private EventFactoryService eventFactoryService;

    @Inject
    private SessionFactory sessionFactory;

    @BeforeEach
    public void before() {
        NtRpgPlugin.GlobalScope.eventFactory = eventFactoryService;
        new TestDictionary().reset();
    }

    @Test
    public void testAttributeAdd(@Stage(READY)IActiveCharacter iActiveCharacter) {
        iActiveCharacter.setAttributePoints(0);
        ActionResult i = characterService.addAttribute(iActiveCharacter, TestDictionary.AGI);
        Integer attributeValue = iActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 99);
        Assertions.assertFalse(i.isOk());
    }

    @Test
    public void testAttributeAdd2(@Stage(READY)IActiveCharacter iActiveCharacter) {
        iActiveCharacter.setAttributePoints(1);
        HashMap<AttributeConfig, Integer> map = new HashMap<>();
        map.put(TestDictionary.AGI, 2);
        ActionResult i = characterService.addAttribute(iActiveCharacter, map);
        Integer attributeValue = iActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 99);
        Assertions.assertFalse(i.isOk());
    }

    @Test
    public void testAttributeAdd_ok(@Stage(READY)IActiveCharacter iActiveCharacter) {
        iActiveCharacter.getCharacterBase().setUuid(UUID.randomUUID());
        characterService.createAndUpdate(iActiveCharacter.getCharacterBase());
        iActiveCharacter.setAttributePoints(2);
        HashMap<AttributeConfig, Integer> map = new HashMap<>();
        map.put(TestDictionary.AGI, 2);
        ActionResult i = characterService.addAttribute(iActiveCharacter, map);

        Integer attributeValue = iActiveCharacter.getAttributeValue(TestDictionary.AGI);
        Assertions.assertEquals(attributeValue, 101);
        Assertions.assertTrue(i.isOk());
        Assertions.assertEquals(iActiveCharacter.getAttributePoints(),0);
        Assertions.assertEquals(iActiveCharacter.getCharacterBase().getAttributePointsSpent(),2);
        Assertions.assertTrue(iActiveCharacter.requiresDamageRecalculation());

        characterService.putInSaveQueue(iActiveCharacter.getCharacterBase());
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Query query = session.createQuery("from BaseCharacterAttribute where characterBase = :base");
        query.setParameter("base", iActiveCharacter.getCharacterBase());

        List result = query.getResultList();

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
