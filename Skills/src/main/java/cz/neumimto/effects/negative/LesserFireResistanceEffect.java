package cz.neumimto.effects.negative;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import org.spongepowered.api.entity.EntityTypes;

/**
 * Created by NeumimTo on 28.3.2017.
 */
@ClassGenerator.Generate(id = "name")
public class LesserFireResistanceEffect extends EffectBase<Float> {

	public static final String name = "Decreased fire resistance";

	public LesserFireResistanceEffect(IEffectConsumer consumer, long duration, float percentage) {
		super(name, consumer);
		setDuration(duration);
		setValue(percentage);
		setStackable(true, new FloatEffectStackingStrategy());
	}

	public LesserFireResistanceEffect(IActiveCharacter character, long duration, String level) {
		this(character, duration, Float.parseFloat(level));
	}

	@Override
	public void onApply() {
		float characterProperty = getConsumer().getProperty(DefaultProperties.fire_damage_protection_mult);
		getConsumer().setProperty(DefaultProperties.fire_damage_protection_mult, characterProperty - getValue());
	}

	@Override
	public void onRemove() {
		float characterProperty = getConsumer().getProperty(DefaultProperties.fire_damage_protection_mult);
		getConsumer().setProperty(DefaultProperties.fire_damage_protection_mult, characterProperty + getValue());
	}


}

