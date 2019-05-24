package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.properties.DefaultProperties;
import cz.neumimto.rpg.common.scripting.JsBinding;

/**
 * Created by NeumimTo on 28.3.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Increases resistance against fire damage")
public class FireResistanceEffect extends SingleResistanceValueEffect {

	public static final String name = "Fire Resistance";

	public FireResistanceEffect(IEffectConsumer consumer, long duration, float percentage) {
		super(name, consumer, DefaultProperties.fire_damage_protection_mult, percentage);
		setDuration(duration);
	}
}

