package cz.neumimto.rpg.api.events.effect;

import cz.neumimto.rpg.api.effects.IEffect;

public interface TargetEffectEvent extends TargetIEffectConsumer {

    IEffect getEffect();

    IEffect setEffect(IEffect iEffect);
}
