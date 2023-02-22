package cz.neumimto.rpg;

import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.common.effects.stacking.DoubleEffectStackingStrategy;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.effects.TestEffectService;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.model.CharacterBaseTest;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Matchers.any;


@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class EffectTests {

    private static TestEffectService effectService = new TestEffectService();

    private static TickableEffect effect;

    static ActiveCharacter character;
    static CharacterBase characterBase;

    private static Set<IEffect> processedEffects;


    @BeforeEach
    public void before() {
        processedEffects = effectService.getEffects();
        characterBase = new CharacterBaseTest();
        character = new TestCharacter(UUID.randomUUID(), characterBase, 1);

        effect = createEffectMock("testEffect");
    }

    private static TickableEffect createEffectMock(String name) {

        TickableEffect effect = Mockito.spy(new TickableEffect(name, character, Long.MAX_VALUE, 1));
        Mockito.when(effect.getExpireTime()).thenReturn(Long.MAX_VALUE);
        return effect;
    }

    @Test
    public void test_Effect_Expirable_unstackable() {
        effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
        effect.setPeriod(0);
        effectService.schedule();
        effectService.schedule();
        Assertions.assertNotNull(character.getEffect(effect.getName()));
        Assertions.assertNotSame(effect, character.getEffect(effect.getName()));

        Mockito.verify(effect, Mockito.times(1)).onApply(any());
        Mockito.verify(effect, Mockito.times(0)).onTick(any());
        Mockito.verify(effect, Mockito.times(0)).onRemove(any());

        Mockito.when(effect.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();
        Mockito.verify(effect, Mockito.times(1)).onRemove(any());
        Assertions.assertNull(character.getEffect(effect.getName()));
        Assertions.assertTrue(processedEffects.isEmpty());
    }

    @Test
    public void test_Effect_Expirable_tickable_unstackable() {
        Mockito.when(effect.isTickingDisabled()).thenReturn(false);
        Mockito.when(effect.getPeriod()).thenReturn(1L);

        effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);

        effectService.schedule();
        effectService.schedule();

        Assertions.assertNotNull(character.getEffect(effect.getName()));
        Assertions.assertNotSame(effect, character.getEffect(effect.getName()));
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
        Assertions.assertNull(character.getEffect(effect.getName()));

        effectService.schedule();
        Assertions.assertTrue(processedEffects.isEmpty());
    }

    @Test
    public void test_Effect_Expirable_stackable_single_instance() {
        effect = (TickableEffect) makeEffectStackable(effect);
        effect.setPeriod(0);
        effect.setPeriod(1);
        effect.setDuration(10000);
        effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
        effectService.schedule();
        effectService.schedule();
        Assertions.assertNotNull(character.getEffect(effect.getName()));
        Assertions.assertNotSame(effect, character.getEffect(effect.getName()));

        Mockito.verify(effect, Mockito.times(1)).onApply(any());
        Mockito.verify(effect, Mockito.times(1)).onTick(any());
        Mockito.verify(effect, Mockito.times(0)).onRemove(any());

        Mockito.when(effect.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();
        Mockito.verify(effect, Mockito.times(1)).onRemove(any());
        Assertions.assertNull(character.getEffect(effect.getName()));
        Assertions.assertTrue(processedEffects.isEmpty());
    }


    @Test
    public void test_Effect_Expirable_stackable() {
        IEffect first = makeEffectStackable(this.effect);
        Mockito.when(first.getExpireTime()).thenReturn(System.currentTimeMillis() + 100000L);

        effectService.addEffect(first, InternalEffectSourceProvider.INSTANCE);

        Assertions.assertNotNull(character.getEffect(first.getName()));
        Assertions.assertNotSame(first, character.getEffect(first.getName()));
        Assertions.assertEquals(1D, character.getEffect("testEffect").getStackedValue());

        effectService.schedule();

        Mockito.verify(first, Mockito.times(1)).onApply(any());
        Mockito.verify(first, Mockito.times(0)).onTick(any());
        Mockito.verify(first, Mockito.times(0)).onRemove(any());


        IEffect test = createEffectMock("testEffect");
        makeEffectStackable(test);
        Mockito.when(test.getExpireTime()).thenReturn(System.currentTimeMillis() + 100000L);

        effectService.addEffect(test, InternalEffectSourceProvider.INSTANCE);
        Mockito.verify(test, Mockito.times(1)).onApply(any());
        Mockito.verify(first, Mockito.times(1)).onApply(any());
        effectService.schedule();

        Assertions.assertEquals(2D, character.getEffect("testEffect").getStackedValue());
        Assertions.assertEquals(2, processedEffects.size());
        Assertions.assertEquals(1, character.getEffectMap().size());
        Assertions.assertEquals(2, character.getEffect("testEffect").getEffects().size());

        //expire
        Mockito.when(test.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();

        Mockito.verify(test, Mockito.times(1)).onRemove(any());

        Assertions.assertSame(processedEffects.size(), 1);
        Assertions.assertSame(character.getEffectMap().size(), 1);
        Assertions.assertTrue(character.getEffect("testEffect").getStackedValue().equals(1D));

        Mockito.when(first.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();
        Mockito.verify(first, Mockito.times(1)).onRemove(any());

        Assertions.assertSame(processedEffects.size(), 0);
        Assertions.assertSame(character.getEffectMap().size(), 0);
        Assertions.assertNull(character.getEffect("testEffect"));
    }

    private IEffect makeEffectStackable(IEffect effect) {
        effect.setStackable(true, DoubleEffectStackingStrategy.INSTANCE);
        effect.setValue(1D);
        effect = Mockito.spy(effect);
        Mockito.when(effect.getConsumer()).thenReturn(character);
        return effect;
    }
}
