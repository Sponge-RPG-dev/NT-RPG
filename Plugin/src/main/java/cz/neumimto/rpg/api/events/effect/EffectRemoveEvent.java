package cz.neumimto.rpg.api.events.effect;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when an {@link IEffect} is removed from {@link IEffectConsumer}.
 * Contains {@link IEffect} in {@link Cause}
 * <p>
 * To filter specific effects in listener use EffectRemoveEvent<YourEffect> or "@First YourEffect"
 */
public interface EffectRemoveEvent {

}
