package cz.neumimto.rpg.effects;

/**
 * Created by fs on 30.3.17.
 */
public interface EffectStackingStrategy<U> {

    void onStackLevelIncrease(U u);

    void onStackLevelDecrease(U u);

}
