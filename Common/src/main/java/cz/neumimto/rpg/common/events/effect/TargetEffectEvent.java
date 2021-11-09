package cz.neumimto.rpg.common.events.effect;

import cz.neumimto.rpg.common.effects.IEffect;

public interface TargetEffectEvent<T extends IEffect> extends TargetIEffectConsumer {

    T getEffect();

    void setEffect(T iEffect);

}
