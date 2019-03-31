package cz.neumimto.rpg.events.effect;

import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

/**
 * Called when an {@link IEffect} is applied to {@link IEffectConsumer}.
 * Contains {@link IEffectSourceProvider} in {@link Cause}
 */
public class EffectApplyEvent extends AbstractEffectEvent implements Cancellable {
	protected boolean cancelled;

	public EffectApplyEvent(IEffect effect) {
		super(effect);
		setCause(Cause.builder()
				.append(effect.getEffectSourceProvider())
				.from(cause)
				.build(EventContext.empty()));
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
