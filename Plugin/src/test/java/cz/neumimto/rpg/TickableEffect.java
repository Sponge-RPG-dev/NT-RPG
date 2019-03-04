package cz.neumimto.rpg;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;

public class TickableEffect extends EffectBase<Long> {

    public static String name = "Test";

    public TickableEffect() {
    }

    public TickableEffect(IEffectConsumer character, long duration, long model) {
        super(name, character);
        setDuration(duration);
        setPeriod(model);
    }

}
