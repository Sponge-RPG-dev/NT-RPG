package cz.neumimto.rpg;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.common.commands.CharacterCommands;
import cz.neumimto.rpg.common.effects.AbstractEffectService;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.CharactersExtension.Stage;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class CharacterCommandTests {

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Inject
    private CharacterCommands characterCommands;

    @Inject
    private AbstractEffectService effectService;

    @Inject
    private IPlayerMessage vanillaMessaging;

    @Inject
    private RpgApi rpgApi;

    @Inject
    private PropertyServiceImpl propertyService;

    @BeforeEach
    public void before() {
        new RpgTest(rpgApi);
        new Gui(vanillaMessaging);
    }

    @Test
    public void testCommandAddAttribute(@Stage(READY) IActiveCharacter iActiveCharacter) {


        AttributeConfig attributeConfig = new AttributeConfig("test", "test", 100, false, Collections.emptyMap(), "test", "test");
        propertyService.getAttributes().put(attributeConfig.getId(), attributeConfig);

        iActiveCharacter.getCharacterBase().setAttributePoints(1);
        iActiveCharacter.getTransientAttributes().put("test", 0);
        Integer value = iActiveCharacter.getAttributeValue(attributeConfig);
        HashMap<AttributeConfig, Integer> map = new HashMap<>();
        map.put(attributeConfig, 1);
        iActiveCharacter.getAttributesTransaction().put("test", 1);
        characterCommandFacade.commandCommitAttribute(iActiveCharacter);

        Assertions.assertEquals(iActiveCharacter.getAttributeValue(attributeConfig), value + 1);
        Assertions.assertEquals(iActiveCharacter.getCharacterBase().getAttributePoints(), 0);
        Assertions.assertEquals(iActiveCharacter.getCharacterBase().getAttributePointsSpent(), 1);
    }

    @Test
    public void testAddExpCommand(@Stage(READY) IActiveCharacter iActiveCharacter) {
        ClassDefinition classDefinition = new ClassDefinition("test", Rpg.get().getPluginConfig().CLASS_TYPES.keySet().iterator().next());
        characterCommands.chooseCharacterClass(iActiveCharacter, classDefinition);
        Assertions.assertTrue(iActiveCharacter.getClasses().containsKey("test"));
    }

    @Test
    public void testAddExpCommand_Wrong_Order(@Stage(READY) IActiveCharacter iActiveCharacter) {
        Iterator<String> iterator = Rpg.get().getPluginConfig().CLASS_TYPES.keySet().iterator();
        iterator.next();
        String i = iterator.next();
        ClassDefinition classDefinition = new ClassDefinition("test", i);
        Rpg.get().getPluginConfig().RESPECT_CLASS_SELECTION_ORDER = true;
        characterCommands.chooseCharacterClass(iActiveCharacter, classDefinition);
        Assertions.assertFalse(iActiveCharacter.getClasses().containsKey("test"));
    }

    @Test
    public void testCharacterCreated() {
        CountDownLatch latch = new CountDownLatch(1);
        characterCommandFacade.commandCreateCharacter(UUID.randomUUID(), "test", "", actionResult -> {
            Log.info(actionResult.getMessage());
            Assertions.assertTrue(actionResult.isOk());
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(latch.getCount(), 0);
    }
}
