package cz.neumimto.rpg.api.events.effect;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.IEntity;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when an {@link IEffect} is applied to {@link IEffectConsumer}.
 * Contains {@link IEffect}, {@link IEffectSourceProvider} and sometimes {@link IEntity} in {@link Cause}
 * <p>
 * To filter specific effects in listener use EffectApplyEvent<YourEffect> or "@First YourEffect"
 */
public interface EffectApplyEvent<T extends IEffect>  {

}
