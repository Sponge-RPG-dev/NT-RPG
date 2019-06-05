package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;

/**
 * Created by NeumimTo on 30.12.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class ConductivityEffect extends SingleResistanceValueEffect {

	public static final String name = "Conductivity";

	public ConductivityEffect(IEffectConsumer consumer, long duration, Float value) {
		super(name, consumer, SpongeDefaultProperties.lightning_damage_protection_mult, value);
		setDuration(duration);
	}
}
