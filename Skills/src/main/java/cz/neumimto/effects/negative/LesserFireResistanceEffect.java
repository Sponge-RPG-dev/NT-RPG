package cz.neumimto.effects.negative;

import cz.neumimto.effects.SingleResistanceValueEffect;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.EntityTypes;

/**
 * Created by NeumimTo on 28.3.2017.
 */
@ClassGenerator.Generate(id = "name")
public class LesserFireResistanceEffect extends SingleResistanceValueEffect {

	public static final String name = "Lesser Fire Resistance";

	public LesserFireResistanceEffect(IEffectConsumer consumer, long duration, float percentage) {
		super(name, consumer, DefaultProperties.fire_damage_protection_mult, percentage);
		setDuration(duration);
	}

	public LesserFireResistanceEffect(IActiveCharacter character, long duration, String level) {
		this(character, duration, Float.parseFloat(Utils.extractNumber(level)));
	}
}

