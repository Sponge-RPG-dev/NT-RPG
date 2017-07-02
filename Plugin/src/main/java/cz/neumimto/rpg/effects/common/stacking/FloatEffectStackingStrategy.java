package cz.neumimto.rpg.effects.common.stacking;

import cz.neumimto.rpg.effects.EffectStackingStrategy;

/**
 * Created by NeumimTo on 2.4.2017.
 */
public class FloatEffectStackingStrategy implements EffectStackingStrategy<Float> {

	@Override
	public Float mergeValues(Float current, Float toAdd) {
		return current + toAdd;
	}

	@Override
	public Float getDefaultValue() {
		return 0f;
	}
}
