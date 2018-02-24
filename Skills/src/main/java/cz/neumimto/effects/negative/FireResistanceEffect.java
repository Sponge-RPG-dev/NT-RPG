package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.utils.Utils;

/**
 * Created by NeumimTo on 28.3.2017.
 */
@ClassGenerator.Generate(id = "name")
public class FireResistanceEffect extends SingleResistanceValueEffect {

	public static final String name = "Fire Resistance";

	public FireResistanceEffect(IEffectConsumer consumer, long duration, float percentage) {
		super(name, consumer, DefaultProperties.fire_damage_protection_mult, percentage);
		setDuration(duration);
	}
}

