package cz.neumimto.rpg.effects.common.stacking;

import cz.neumimto.rpg.api.EffectStackingStrategy;

/**
 * Created by NeumimTo on 2.4.2017.
 */
public class DoubleEffectStackingStrategy implements EffectStackingStrategy<Double> {

	public static final DoubleEffectStackingStrategy INSTANCE = new DoubleEffectStackingStrategy();

	private DoubleEffectStackingStrategy() {
	}

	@Override
	public Double mergeValues(Double current, Double toAdd) {
		return current + toAdd;
	}

	@Override
	public Double getDefaultValue() {
		return 0d;
	}
}
