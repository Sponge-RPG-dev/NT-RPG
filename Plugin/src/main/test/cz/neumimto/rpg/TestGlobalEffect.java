package cz.neumimto.rpg;

import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.common.def.ClickComboActionEvent;
import cz.neumimto.rpg.effects.model.EffectModelFactory;

import java.util.Map;

public class TestGlobalEffect implements IGlobalEffect<TestEffect> {

    @Override
    public TestEffect construct(IEffectConsumer consumer, long duration, Map<String, String> data) {
        return new TestEffect(consumer, duration, EffectModelFactory.create(TestEffect.class, data, Float.class));
    }

    @Override
    public String getName() {
        return "safd";
    }

    @Override
    public Class<TestEffect> asEffectClass() {
        return TestEffect.class;
    }
}
