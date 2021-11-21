package cz.neumimto.rpg;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.skills.scripting.EffectScriptGenerator;
import cz.neumimto.rpg.common.skills.scripting.ScriptEffectModel;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.lang.reflect.Modifier;
import java.util.HashMap;

@ExtendWith({GuiceExtension.class})
@IncludeModule(TestGuiceModule.class)
public class TestEffectScripts {

    @Inject
    private RpgApi rpgApi;

    @BeforeEach
    public void before() {
        new RpgTest(rpgApi);
    }


    @Test
    public void test_noMethod_gen() throws Exception {
        ScriptEffectModel model = new ScriptEffectModel();
        model.id = "Test";
        model.fields = new HashMap<>();
        model.fields.put("Num", "numeric");
        Class<? extends IEffect> from = EffectScriptGenerator.from(model, this.getClass().getClassLoader());
        IEffect iEffect = from.newInstance();
        Assertions.assertEquals(model.id, iEffect.getName());
        Assertions.assertTrue(iEffect.getClass().getConstructor().isAnnotationPresent(ScriptMeta.ScriptTarget.class));
    }

    @Test
    public void test_method_gen() throws Exception {
        ScriptEffectModel model = new ScriptEffectModel();
        model.id = "Test";
        model.fields = new HashMap<>();
        model.fields.put("Num", "numeric");
        model.onApply = """
                @effect.Num = 50
                RETURN
                """;
        Class<? extends IEffect> from = EffectScriptGenerator.from(model, this.getClass().getClassLoader());
        IEffect iEffect = from.newInstance();

        Assertions.assertEquals(model.id, iEffect.getName());
        Assertions.assertTrue(iEffect.getClass().getConstructor().isAnnotationPresent(ScriptMeta.ScriptTarget.class));

        Assertions.assertSame(iEffect.getClass().getField("Num").getType(), double.class);

        Assertions.assertNotNull(iEffect.getClass().getDeclaredField("onApply"));
        Assertions.assertTrue(Modifier.isStatic(iEffect.getClass().getDeclaredField("onApply").getModifiers()));
        Assertions.assertNotNull(iEffect.getClass().getDeclaredField("onApply").get(null));

        iEffect.onApply(iEffect);
        double d = (double) iEffect.getClass().getField("Num").get(iEffect);
        Assertions.assertEquals(d, 50);

    }
}
