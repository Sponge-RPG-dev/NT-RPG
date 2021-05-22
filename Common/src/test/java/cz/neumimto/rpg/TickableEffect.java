package cz.neumimto.rpg;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.EffectContainer;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

public class TickableEffect extends EffectBase<Double> {

    public static String name = "Test";

    public TickableEffect(String name, IEffectConsumer character, long duration, double model) {
        super(name, character);
        setDuration(duration);
        setPeriod((long) model);
        setValue(model);
    }

    @Override
    public IEffectContainer constructEffectContainer() {
        return new EffectContainer<>(this);
    }
}
