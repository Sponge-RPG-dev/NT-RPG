package cz.neumimto.rpg.api.events.effect;

import cz.neumimto.rpg.api.effects.IEffect;

public interface TargetEffectEvent<T extends IEffect> extends TargetIEffectConsumer {

    T getEffect();

    void setEffect(T iEffect);
}
