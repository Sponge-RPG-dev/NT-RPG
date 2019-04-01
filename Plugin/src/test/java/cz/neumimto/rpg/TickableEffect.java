package cz.neumimto.rpg;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;

public class TickableEffect extends EffectBase<Long> {

    public static String name = "Test";

    public TickableEffect() {
    }

    public TickableEffect(String name,IEffectConsumer character, long duration, long model) {
        super(name, character);
        setDuration(duration);
        setPeriod(model);
    }

    @Override
    public IEffectContainer constructEffectContainer() {
        return new EffectContainer<>(this);
    }
}
