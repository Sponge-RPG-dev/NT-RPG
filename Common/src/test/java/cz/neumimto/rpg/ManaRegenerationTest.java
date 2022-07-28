package cz.neumimto.rpg;

import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.events.EventFactoryService;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.gui.IPlayerMessage;
import cz.neumimto.rpg.common.resources.DefaultManaRegeneration;
import cz.neumimto.rpg.common.resources.ResourceService;
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

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({CharactersExtension.class, GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class ManaRegenerationTest {

    @Inject
    private EffectService iEffectService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private PluginConfig pluginConfig;

    @Inject
    private EntityService entityService;

    @Inject
    private IPlayerMessage vanillaMessaging;

    @Inject
    private EventFactoryService eventFactoryService;

    @Inject
    private RpgApi rpgApi;

    @BeforeEach
    public void before() {
        new RpgTest(rpgApi);
        new Gui(vanillaMessaging);}

    @Test
    public void testManaRegen(@Stage(READY) IActiveCharacter character) {
       DefaultManaRegeneration defaultManaRegeneration = new DefaultManaRegeneration(character);

       iEffectService.addEffect(defaultManaRegeneration);

       iEffectService.schedule(); //put into main loop

       Assertions.assertEquals(character.getResource(ResourceService.mana).getValue(), 50.0);
       iEffectService.schedule();
       Assertions.assertEquals(character.getResource(ResourceService.mana).getValue(), 51.0);

       for (int i = 0; i < 100; i++) {
           iEffectService.schedule();
           try {
               Thread.sleep(2);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
       Assertions.assertEquals(character.getResource(ResourceService.mana).getValue(), 100.0);
    }
}
