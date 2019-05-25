package cz.neumimto.rpg.api.events.effect;

import cz.neumimto.rpg.effects.IEffectConsumer;

/**
 * Base event for when a {@link IEffectConsumer} is a target.
 */
public interface TargetIEffectConsumer {
    IEffectConsumer getTarget();

}
