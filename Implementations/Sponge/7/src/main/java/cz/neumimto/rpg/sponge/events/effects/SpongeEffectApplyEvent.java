package cz.neumimto.rpg.sponge.events.effects;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.effect.EffectApplyEvent;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when an {@link IEffect} is applied to {@link IEffectConsumer}.
 * Contains {@link IEffect}, {@link IEffectSourceProvider} and sometimes {@link IEntity} in {@link Cause}
 * <p>
 * To filter specific effects in listener use EffectApplyEvent<YourEffect> or "@First YourEffect"
 */
public class SpongeEffectApplyEvent<T extends IEffect> extends AbstractEffectEvent<T> implements EffectApplyEvent<T>, Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
    }
}
