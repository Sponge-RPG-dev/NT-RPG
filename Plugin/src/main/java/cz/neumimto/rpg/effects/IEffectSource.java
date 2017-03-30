package cz.neumimto.rpg.effects;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public interface IEffectSource<T, U extends EffectStackingStrategy> {

    EffectSourceType getEffectSourceType();

    T getSource();

    U getEffectStackStrategy();

}
