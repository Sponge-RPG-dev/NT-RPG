package cz.neumimto.rpg.common.effects;

/**
 * Created by NeumimTo on 30.3.17.
 */
public interface EffectStackingStrategy<U> {

    U mergeValues(U current, U toAdd);

    default U getDefaultValue() {
        return null;
    }
}
