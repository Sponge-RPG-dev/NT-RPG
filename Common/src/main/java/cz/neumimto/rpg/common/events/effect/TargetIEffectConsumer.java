package cz.neumimto.rpg.common.events.effect;

import cz.neumimto.rpg.common.entity.IEffectConsumer;

/**
 * Base event for when a {@link IEffectConsumer} is a target.
 */
public interface TargetIEffectConsumer {

    IEffectConsumer getTarget();

}
