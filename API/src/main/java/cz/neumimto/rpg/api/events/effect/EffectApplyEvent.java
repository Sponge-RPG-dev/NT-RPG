package cz.neumimto.rpg.api.events.effect;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.events.Cancellable;

public interface EffectApplyEvent<T extends IEffect> extends TargetEffectEvent<T>, Cancellable {

}
