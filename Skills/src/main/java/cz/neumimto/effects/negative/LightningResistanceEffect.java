package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;

/**
 * Created by NeumimTo on 1.8.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Increases resistance against lightning damage")
public class LightningResistanceEffect extends SingleResistanceValueEffect {

	public static final String name = "Lightning resistance";

	public LightningResistanceEffect(IEffectConsumer consumer, long duration, float value) {
		super(name, consumer, SpongeDefaultProperties.lightning_damage_protection_mult, value);
		setDuration(duration);
	}
}
