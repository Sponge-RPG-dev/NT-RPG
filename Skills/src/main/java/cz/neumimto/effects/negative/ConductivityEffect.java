package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.properties.DefaultProperties;
import cz.neumimto.rpg.common.scripting.JsBinding;

/**
 * Created by NeumimTo on 30.12.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class ConductivityEffect extends SingleResistanceValueEffect {

	public static final String name = "Conductivity";

	public ConductivityEffect(IEffectConsumer consumer, long duration, Float value) {
		super(name, consumer, DefaultProperties.lightning_damage_protection_mult, value);
		setDuration(duration);
	}
}
