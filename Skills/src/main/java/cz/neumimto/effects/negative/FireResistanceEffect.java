package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;

/**
 * Created by NeumimTo on 28.3.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Increases resistance against fire damage")
public class FireResistanceEffect extends SingleResistanceValueEffect {

	public static final String name = "Fire Resistance";

	public FireResistanceEffect(IEffectConsumer consumer, long duration, float percentage) {
		super(name, consumer, SpongeDefaultProperties.fire_damage_protection_mult, percentage);
		setDuration(duration);
	}
}

