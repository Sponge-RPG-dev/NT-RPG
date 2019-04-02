package cz.neumimto.effects.positive;

import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.properties.DefaultProperties;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 3.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Increments walk speed, unlike speedboost effect this one wont apply vanilla potion effect")
public class IncreasedMovementSpeedEffect extends EffectBase<Float> {

	public static final String name = "Movement Speed";

	public IncreasedMovementSpeedEffect(IEffectConsumer consumer, long duration, float value) {
		super(name, consumer);
		setValue(value);
		setDuration(duration);
		setStackable(true, FloatEffectStackingStrategy.INSTANCE);
	}

	@Override
	public void onApply(IEffect self) {
		getConsumer().setProperty(DefaultProperties.walk_speed, getConsumer().getProperty(DefaultProperties.walk_speed) + getValue());
		getGlobalScope().entityService.updateWalkSpeed(getConsumer());
	}

	@Override
	public void onRemove(IEffect self) {
		getConsumer().setProperty(DefaultProperties.walk_speed, getConsumer().getProperty(DefaultProperties.walk_speed) - getValue());
		getGlobalScope().entityService.updateWalkSpeed(getConsumer());
	}

	@Override
	public void setValue(Float o) {
		if (getValue() != null) {
			throw new IllegalStateException("Operation permited");
		}
		super.setValue(o);
	}
}
