package cz.neumimto.rpg;

import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Matchers.any;

public class EffectTests {

    private EffectService effectService = new EffectService();

    ActiveCharacter character;
    CharacterBase characterBase;
    ScheduledExecutorService executorService;

    @BeforeClass
    public static void init() throws Exception {
        TestHelper.initLocalizations();
        NtRpgPlugin.pluginConfig = (PluginConfig) TestHelper.getUnsafe().allocateInstance(PluginConfig.class);
        NtRpgPlugin.pluginConfig.DEBUG = DebugLevel.NONE;
    }

    @Before
    public void before() {
        characterBase = new CharacterBase();
        character = new ActiveCharacter(UUID.randomUUID(), characterBase);
    }

    //@Test
    public void test_Effect_Add() {
        TickableEffect effect = new TickableEffect(character, 3000, 500);
        effect = (TickableEffect) Mockito.spy(effect);
        Mockito.doCallRealMethod().when(effect).constructEffectContainer();

        effectService.addEffect(effect, character, InternalEffectSourceProvider.INSTANCE);
        Assert.assertNotNull(character.getEffect(effect.getName()));
        Assert.assertNotSame(effect, character.getEffect(effect.getName()));
        Mockito.verify(effect, Mockito.times(1)).onApply(any());
    }
}
