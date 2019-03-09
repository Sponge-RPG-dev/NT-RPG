package cz.neumimto.rpg;

import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.effects.common.stacking.MinLongStackingStrategy;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Matchers.any;

public class EffectTests {

    private EffectService effectService = new EffectService();

    private TickableEffect effect;

    ActiveCharacter character;
    CharacterBase characterBase;

    private Set<IEffect> processedEffects;

    @BeforeClass
    public static void init() throws Exception {
        TestHelper.initLocalizations();
        NtRpgPlugin.pluginConfig = (PluginConfig) TestHelper.getUnsafe().allocateInstance(PluginConfig.class);
        NtRpgPlugin.pluginConfig.DEBUG = DebugLevel.NONE;
    }

    @Before
    public void before() throws Exception{
        processedEffects = (Set<IEffect>) TestHelper.getField(effectService, "effectSet");
        characterBase = new CharacterBase();
        character = new ActiveCharacter(UUID.randomUUID(), characterBase);

        effect = Mockito.mock(TickableEffect.class);

        Mockito.when(effect.getConsumer()).thenReturn(character);
        Mockito.when(effect.getDuration()).thenReturn(Long.MAX_VALUE);
        Mockito.when(effect.getExpireTime()).thenReturn(Long.MAX_VALUE);
        Mockito.when(effect.getEffectSourceProvider()).thenReturn(InternalEffectSourceProvider.INSTANCE);
        Mockito.when(effect.getName()).thenReturn("effect");
        Mockito.when(effect.getValue()).thenReturn(1000L);
        Mockito.when(effect.getEffectStackingStrategy()).thenReturn(MinLongStackingStrategy.INSTNCE);
        Mockito.when(effect.requiresRegister()).thenCallRealMethod();

        EffectContainer container = new EffectContainer<>(effect);
        Mockito.when(effect.constructEffectContainer()).thenReturn(container);

    }

    @Test
    public void test_Effect_Add_And_Remove_unstackable() {
        effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
        effectService.schedule();
        effectService.schedule();
        Assert.assertNotNull(character.getEffect(effect.getName()));
        Assert.assertNotSame(effect, character.getEffect(effect.getName()));

        Mockito.verify(effect, Mockito.times(1)).onApply(any());
        Mockito.verify(effect, Mockito.times(0)).onTick(any());
        Mockito.verify(effect, Mockito.times(0)).onRemove(any());

        Mockito.when(effect.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();
        Mockito.verify(effect, Mockito.times(1)).onRemove(any());
        Assert.assertNull(character.getEffect(effect.getName()));
        Assert.assertTrue(processedEffects.isEmpty());
    }

    @Test
    public void testEffect_Add_And_Remove_tickable_unstackable() {
        Mockito.when(effect.isTickingDisabled()).thenReturn(false);
        Mockito.when(effect.getPeriod()).thenReturn(1L);

        effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);

        effectService.schedule();
        effectService.schedule();

        Assert.assertNotNull(character.getEffect(effect.getName()));
        Assert.assertNotSame(effect, character.getEffect(effect.getName()));
        Mockito.verify(effect, Mockito.times(1)).onApply(any());
        Mockito.verify(effect, Mockito.times(1)).onTick(any());
        Mockito.verify(effect, Mockito.times(0)).onRemove(any());

        Mockito.when(effect.getLastTickTime()).thenReturn(0L);

        effectService.schedule();

        Mockito.verify(effect, Mockito.times(2)).onTick(any());

        effectService.schedule();
        Mockito.verify(effect, Mockito.times(3)).onTick(any());

        Mockito.when(effect.getExpireTime()).thenReturn(0L);

        effectService.schedule();
        Mockito.verify(effect, Mockito.times(4)).onTick(any());
        Mockito.verify(effect, Mockito.times(1)).onRemove(any());

        Mockito.verify(effect, Mockito.times(1)).onRemove(any());
        Assert.assertNull(character.getEffect(effect.getName()));

        effectService.schedule();
        Assert.assertTrue(processedEffects.isEmpty());
    }
}
