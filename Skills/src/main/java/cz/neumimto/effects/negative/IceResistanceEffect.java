package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 1.8.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@ClassGenerator.Generate(id = "name", description = "Increases resistance against ice damage")
public class IceResistanceEffect extends SingleResistanceValueEffect {

	public static final String name = "Ice Resistance";

	public IceResistanceEffect(IEffectConsumer consumer, long duration, float value) {
		super(name, consumer, DefaultProperties.ice_damage_protection_mult, value);
		setDuration(duration);
	}

}
