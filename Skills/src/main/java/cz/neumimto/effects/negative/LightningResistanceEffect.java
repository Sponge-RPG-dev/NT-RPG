package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.utils.Utils;

/**
 * Created by NeumimTo on 1.8.2017.
 */
@ClassGenerator.Generate(id = "name")
public class LightningResistanceEffect extends SingleResistanceValueEffect {

	public static final String name = "Lightning resistance";

	public LightningResistanceEffect(IEffectConsumer consumer, long duration, float value) {
		super(name, consumer, DefaultProperties.lightning_damage_protection_mult, value);
		setDuration(duration);
	}

	public LightningResistanceEffect(IActiveCharacter character, long duration, String level) {
		this(character, duration, Float.parseFloat(Utils.extractNumber(level)));
	}
}
