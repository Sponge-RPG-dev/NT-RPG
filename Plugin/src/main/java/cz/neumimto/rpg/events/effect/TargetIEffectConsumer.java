package cz.neumimto.rpg.events.effect;

import cz.neumimto.rpg.effects.IEffectConsumer;
import org.spongepowered.api.event.Event;

/**
 * Base event for when a {@link IEffectConsumer} is a target.
 */
public interface TargetIEffectConsumer extends Event {
    IEffectConsumer getTarget();
}
