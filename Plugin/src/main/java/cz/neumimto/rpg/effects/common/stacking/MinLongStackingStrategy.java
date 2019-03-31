package cz.neumimto.rpg.effects.common.stacking;

import cz.neumimto.rpg.effects.EffectStackingStrategy;

/**
 * Created by fs on 7.8.17.
 */
public class MinLongStackingStrategy implements EffectStackingStrategy<Long> {

	@Override
	public Long mergeValues(Long current, Long toAdd) {
		return Math.min(current, toAdd);
	}

	@Override
	public Long getDefaultValue() {
		return 0L;
	}
}
