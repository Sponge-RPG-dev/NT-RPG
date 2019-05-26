package cz.neumimto.rpg.sponge.events.effects;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.events.effect.EffectRemoveEvent;
import cz.neumimto.rpg.api.events.effect.TargetEffectEvent;
import cz.neumimto.rpg.effects.IEffectConsumer;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when an {@link IEffect} is removed from {@link IEffectConsumer}.
 * Contains {@link IEffect} in {@link Cause}
 * <p>
 * To filter specific effects in listener use EffectRemoveEvent<YourEffect> or "@First YourEffect"
 */
public class SpongeEffectRemoveEvent<T extends IEffect> extends AbstractEffectEvent<T> implements EffectRemoveEvent {

}
