package cz.neumimto.rpg.common.events.effect;

import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.events.Cancellable;

public interface EffectApplyEvent<T extends IEffect> extends TargetEffectEvent<T>, Cancellable {

}
