package cz.neumimto.rpg;

import static org.mockito.Matchers.any;
import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.effects.common.stacking.MinLongStackingStrategy;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.*;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Files;
import java.util.Set;
import java.util.UUID;
import javax.script.Invocable;
import javax.script.ScriptEngine;

@Ignore
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
        NtRpgPlugin.GlobalScope = new GlobalScope();
        NtRpgPlugin.GlobalScope.plugin = new NtRpgPlugin();
    }

    @Before
    public void before() throws Exception{
        processedEffects = (Set<IEffect>) TestHelper.getField(effectService, "effectSet");
        characterBase = new CharacterBase();
        character = new ActiveCharacter(UUID.randomUUID(), characterBase);

        effect = createEffectMock("test");

    }

    private TickableEffect createEffectMock(String name) {
        TickableEffect mock = Mockito.mock(TickableEffect.class);

        Mockito.when(mock.getConsumer()).thenReturn(character);
        Mockito.when(mock.getDuration()).thenReturn(Long.MAX_VALUE);
        Mockito.when(mock.getExpireTime()).thenReturn(Long.MAX_VALUE);
        Mockito.when(mock.getEffectSourceProvider()).thenReturn(InternalEffectSourceProvider.INSTANCE);
        Mockito.when(mock.getName()).thenReturn(name);
        Mockito.when(mock.getValue()).thenReturn(1L);
        Mockito.when(mock.getEffectStackingStrategy()).thenReturn(MinLongStackingStrategy.INSTANCE);
        Mockito.when(mock.requiresRegister()).thenCallRealMethod();

        Mockito.when(mock.constructEffectContainer()).thenCallRealMethod();

        return mock;
    }

    @Test
    public void test_Effect_Expirable_unstackable() {
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
    public void test_Effect_Expirable_tickable_unstackable() {
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

    @Test
    public void test_Effect_Expirable_stackable_single_instance() {
        makeEffectStackable(effect);

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
        Assert.assertNotNull(character.getEffect(first.getName()));
        Assert.assertNotSame(first, character.getEffect(first.getName()));
        Assert.assertTrue(character.getEffect("test").getStackedValue().equals(1L));
        effectService.schedule();

        Mockito.verify(first, Mockito.times(1)).onApply(any());
        Mockito.verify(first, Mockito.times(0)).onTick(any());
        Mockito.verify(first, Mockito.times(0)).onRemove(any());


        makeEffectStackable(test);

        effectService.addEffect(test, InternalEffectSourceProvider.INSTANCE);
        Mockito.verify(test, Mockito.times(1)).onApply(any());
        Mockito.verify(first, Mockito.times(1)).onApply(any());
        effectService.schedule();

        Assert.assertTrue(character.getEffect("test").getStackedValue().equals(2L));
        Assert.assertEquals(processedEffects.size(), 2);
        Assert.assertEquals(character.getEffectMap().size(), 1);
        Assert.assertEquals(character.getEffect("test").getEffects().size(), 2);

        //expire
        Mockito.when(test.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();

        Mockito.verify(test, Mockito.times(1)).onRemove(any());

        Assert.assertEquals(processedEffects.size(), 1);
        Assert.assertEquals(character.getEffectMap().size(), 1);
        Assert.assertTrue(character.getEffect("test").getStackedValue().equals(1L));

        Mockito.when(first.getExpireTime()).thenReturn(0L);
        effectService.schedule();
        effectService.schedule();
        Mockito.verify(first, Mockito.times(1)).onRemove(any());

        Assert.assertEquals(processedEffects.size(), 0);
        Assert.assertEquals(character.getEffectMap().size(), 0);
        Assert.assertNull(character.getEffect("test"));
    }

    private void makeEffectStackable(IEffect effect) {
        Mockito.when(effect.isStackable()).thenReturn(true);
        Mockito.when(effect.getEffectStackingStrategy()).thenReturn(new EffectStackingStrategy<Long>() {
            @Override
            public Long mergeValues(Long current, Long toAdd) {
                return current == null ? 1 : toAdd + current;
            }
        });
        Mockito.when(effect.getValue()).thenReturn(1L);
    }
}
