package cz.neumimto.rpg;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.core.DefaultManaRegeneration;
import cz.neumimto.rpg.common.entity.PropertyService;
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
        new Gui(vanillaMessaging);
        Rpg.get().getPluginConfig().MANA_REGENERATION_RATE = 1;
    }

    @Test
    public void testManaRegen(@Stage(READY)IActiveCharacter character) {
        DefaultManaRegeneration defaultManaRegeneration = new DefaultManaRegeneration(character);
        character.setProperty(CommonProperties.mana_regen_mult, 1);
        iEffectService.addEffect(defaultManaRegeneration);

        iEffectService.schedule(); //put into main loop

        Assertions.assertEquals(character.getMana().getValue(), 50.0);
        iEffectService.schedule();
        Assertions.assertEquals(character.getMana().getValue(), 51.0);

        for (int i = 0; i < 100; i++) {
            iEffectService.schedule();
        }
        Assertions.assertEquals(character.getMana().getValue(), 100.0);
    }
}
