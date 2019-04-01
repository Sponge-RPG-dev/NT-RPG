package cz.neumimto.rpg.effects.common.stacking;

import cz.neumimto.rpg.api.EffectStackingStrategy;

/**
 * Created by NeumimTo on 2.4.2017.
 */
public class IntegerEffectStackingStrategy implements EffectStackingStrategy<Integer> {

	public static final IntegerEffectStackingStrategy INSTANCE = new IntegerEffectStackingStrategy();

	private IntegerEffectStackingStrategy() {
	}

	@Override
	public Integer mergeValues(Integer current, Integer toAdd) {
		return current + toAdd;
	}

	@Override
	public Integer getDefaultValue() {
		return 0;
	}
}
