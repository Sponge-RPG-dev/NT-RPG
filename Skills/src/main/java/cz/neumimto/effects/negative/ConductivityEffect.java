package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;

/**
 * Created by NeumimTo on 30.12.2017.
 */
public class ConductivityEffect extends SingleResistanceValueEffect {

    public static final String name = "Conductivity";

    public ConductivityEffect(IEffectConsumer consumer, long duration, String value) {
        this(consumer, duration);
        setValue(duration);
        setDuration(duration);
    }
}
