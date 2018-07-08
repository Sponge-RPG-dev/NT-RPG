package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 3.7.2017.
 */
@ClassGenerator.Generate(id = "name", description = "Increments walk speed, unlike speedboost effect this one wont apply vanilla potion effect")
public class IncreasedMovementSpeedEffect extends EffectBase<Float> {

	public static final String name = "Movement Speed";

	public IncreasedMovementSpeedEffect(IEffectConsumer consumer, long duration, float value) {
		super(name, consumer);
		setValue(value);
		setDuration(duration);
		setStackable(true, new FloatEffectStackingStrategy());
	}

	@Override
	public void onApply() {
		getConsumer().setProperty(DefaultProperties.walk_speed, getConsumer().getProperty(DefaultProperties.walk_speed) + getValue());
		getGlobalScope().characterService.updateWalkSpeed(getConsumer());
	}

	@Override
	public void onRemove() {
		getConsumer().setProperty(DefaultProperties.walk_speed, getConsumer().getProperty(DefaultProperties.walk_speed) - getValue());
		getGlobalScope().characterService.updateWalkSpeed(getConsumer());
	}

	@Override
	public void setValue(Float o) {
		if (getValue() != null)
			throw new IllegalStateException("Operation permited");
		super.setValue(o);
	}
}
