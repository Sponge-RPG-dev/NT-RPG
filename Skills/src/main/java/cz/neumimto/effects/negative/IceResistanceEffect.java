package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;

/**
 * Created by NeumimTo on 1.8.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Increases resistance against ice damage")
public class IceResistanceEffect extends SingleResistanceValueEffect {

	public static final String name = "Ice Resistance";

	public IceResistanceEffect(IEffectConsumer consumer, long duration, float value) {
		super(name, consumer, SpongeDefaultProperties.ice_damage_protection_mult, value);
		setDuration(duration);
	}

}
