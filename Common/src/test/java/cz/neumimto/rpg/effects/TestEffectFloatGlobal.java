package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.effects.model.EffectModelFactory;
import cz.neumimto.rpg.common.entity.IEffectConsumer;

import java.util.Map;

public class TestEffectFloatGlobal implements IGlobalEffect<TestEffectFloat> {
    @Override
    public TestEffectFloat construct(IEffectConsumer consumer, long duration, Map<String, String> data) {
        return new TestEffectFloat(consumer, duration, EffectModelFactory.create(TestEffectFloat.class, data, Float.class));
    }

    @Override
    public String getName() {
        return TestEffectFloat.name;
    }

    @Override
    public Class<TestEffectFloat> asEffectClass() {
        return TestEffectFloat.class;
    }
}
