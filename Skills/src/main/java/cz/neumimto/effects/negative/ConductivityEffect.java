package cz.neumimto.effects.negative;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 30.12.2017.
 */
public class ConductivityEffect extends SingleResistanceValueEffect {

    public static final String name = "Conductivity";

    public ConductivityEffect(IEffectConsumer consumer, long duration, @Inject Float value) {
       	super(name, consumer, DefaultProperties.lightning_damage_protection_mult, value);
        setDuration(duration);
    }
}
