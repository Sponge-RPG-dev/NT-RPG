package cz.neumimto.rpg;

import cz.neumimto.rpg.api.effects.EffectStackingStrategy;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.common.persistance.model.JPACharacterBase;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.common.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.effects.TestEffectService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.File;
import java.nio.file.Files;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Matchers.any;

public class EffectTests {

    private static TestEffectService effectService = new TestEffectService();

    private static TickableEffect effect;

    static ActiveCharacter character;
    static JPACharacterBase characterBase;

    private static Set<IEffect> processedEffects;

    @BeforeEach
    public void init() throws Exception {
        TestHelper.initLocalizations();
        NtRpgPlugin.pluginConfig = (PluginConfig) TestHelper.getUnsafe().allocateInstance(PluginConfig.class);
        NtRpgPlugin.pluginConfig.DEBUG = DebugLevel.NONE;
        NtRpgPlugin.GlobalScope = new GlobalScope();
        NtRpgPlugin.GlobalScope.plugin = new NtRpgPlugin();
    }

    @BeforeAll
    public static void before() throws Exception{
        processedEffects = effectService.getEffects();
        characterBase = new JPACharacterBase();
        character = new ActiveCharacter(UUID.randomUUID(), characterBase, 1);

        effect = createEffectMock("test");

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
        makeEffectStackable(effect);
        effect.setPeriod(0);
        effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
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
    public void test_Effect_Expirable_stackable() {
        IEffect effect = this.effect;
        TickableEffect test = createEffectMock("test");
        processEffectStacking(effect, test);
    }


    public void test_Effect_Expirable_stackable_2_js() throws Exception {
        ScriptEngine scriptEngine = new NashornScriptEngineFactory().getScriptEngine("--optimistic-types=true"/*, "-d=bytecode/"*/);
        IEffect effect = createEffectJsMock("test", scriptEngine);
        IEffect test = createEffectJsMock("test", scriptEngine);
        processEffectStacking(effect, test);
    }

    private IEffect createEffectJsMock(String test, ScriptEngine scriptEngine) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(getClass().getResource("effects/effect01.js").getFile());
        byte[] bytes = Files.readAllBytes(file.toPath());
        scriptEngine.eval(new String(bytes));
        Invocable invocable = (Invocable) scriptEngine;
        ScriptObjectMirror mirror = (ScriptObjectMirror) scriptEngine.eval("SuperNiceEffect");
        return (IEffect) (Object) mirror;
    }

    private void processEffectStacking(IEffect first, IEffect test) {
        makeEffectStackable(first);

        effectService.addEffect(first, InternalEffectSourceProvider.INSTANCE);
        Assertions.assertNotNull(character.getEffect(first.getName()));
        Assertions.assertNotSame(first, character.getEffect(first.getName()));
        Assertions.assertTrue(character.getEffect("test").getStackedValue().equals(1L));
        effectService.schedule();

        Mockito.verify(first, Mockito.times(1)).onApply(any());
        Mockito.verify(first, Mockito.times(0)).onTick(any());
        Mockito.verify(first, Mockito.times(0)).onRemove(any());


        makeEffectStackable(test);

        effectService.addEffect(test, InternalEffectSourceProvider.INSTANCE);
        Mockito.verify(test, Mockito.times(1)).onApply(any());
        Mockito.verify(first, Mockito.times(1)).onApply(any());
        effectService.schedule();

        Assertions.assertTrue(character.getEffect("test").getStackedValue().equals(2L));
        Assertions.assertSame(processedEffects.size(), 2);
        Assertions.assertSame(character.getEffectMap().size(), 1);
        Assertions.assertSame(character.getEffect("test").getEffects().size(), 2);

        //expire
        Mockito.when(test.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();

        Mockito.verify(test, Mockito.times(1)).onRemove(any());

        Assertions.assertSame(processedEffects.size(), 1);
        Assertions.assertSame(character.getEffectMap().size(), 1);
        Assertions.assertTrue(character.getEffect("test").getStackedValue().equals(1L));

        Mockito.when(first.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();
        Mockito.verify(first, Mockito.times(1)).onRemove(any());

        Assertions.assertSame(processedEffects.size(), 0);
        Assertions.assertSame(character.getEffectMap().size(), 0);
        Assertions.assertNull(character.getEffect("test"));
    }

    private void makeEffectStackable(IEffect effect) {
        Mockito.when(effect.isStackable()).thenReturn(true);
        Mockito.when(effect.getEffectStackingStrategy()).thenReturn((EffectStackingStrategy<Long>) (current, toAdd) -> current == null ? 1 : toAdd + current);
        Mockito.when(effect.getValue()).thenReturn(1L);
    }
}
