package cz.neumimto.rpg;

import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.skills.EffectScriptGenerator;
import cz.neumimto.rpg.common.skills.scripting.ScriptEffectModel;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class TestEffectScripts {

    @Test
    public void test_noMethod_gen() {
        ScriptEffectModel model = new ScriptEffectModel();
        model.id = "Test";
        model.fields = new HashMap<>();
        model.fields.put("Num", "double.class");
        Class<? extends IEffect> from = EffectScriptGenerator.from(model, this.getClass().getClassLoader());
    }
}
