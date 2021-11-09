package cz.neumimto.rpg.common.effects.stacking;


import cz.neumimto.rpg.common.effects.EffectStackingStrategy;

/**
 * Created by fs on 7.8.17.
 */
public class MinLongStackingStrategy implements EffectStackingStrategy<Long> {

    public static final MinLongStackingStrategy INSTANCE = new MinLongStackingStrategy();

    private MinLongStackingStrategy() {
    }

    @Override
    public Long mergeValues(Long current, Long toAdd) {
        return Math.min(current, toAdd);
    }

    @Override
    public Long getDefaultValue() {
        return 0L;
    }
}
