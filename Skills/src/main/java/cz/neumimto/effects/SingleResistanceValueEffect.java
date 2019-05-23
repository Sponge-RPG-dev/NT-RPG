package cz.neumimto.effects;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.api.effects.stacking.FloatEffectStackingStrategy;

/**
 * Created NeumimTo ja on 1.8.2017.
 */
public abstract class SingleResistanceValueEffect extends EffectBase<Float> {

	private final int propertyId;

	public SingleResistanceValueEffect(String name, IEffectConsumer consumer, int propertyId, float value) {
		super(name, consumer);
		setStackable(true, FloatEffectStackingStrategy.INSTANCE);
		setValue(value);
		this.propertyId = propertyId;
	}

	public int getPropertyId() {
		return propertyId;
	}

	@Override
	public void onApply(IEffect self) {
		super.onApply(self);
		float characterProperty = getConsumer().getProperty(getPropertyId());
		getConsumer().setProperty(getPropertyId(), characterProperty - getValue());
	}

	@Override
	public void onRemove(IEffect self) {
		super.onRemove(self);
		float characterProperty = getConsumer().getProperty(getPropertyId());
		getConsumer().setProperty(getPropertyId(), characterProperty + getValue());
	}
}
