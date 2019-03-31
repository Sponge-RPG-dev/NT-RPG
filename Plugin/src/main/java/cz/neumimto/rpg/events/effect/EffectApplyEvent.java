package cz.neumimto.rpg.events.effect;

import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when an {@link IEffect} is applied to {@link IEffectConsumer}.
 * Contains {@link IEffect}, {@link IEffectSourceProvider} and sometimes {@link IEntity} in {@link Cause}
 *
 * To filter specific effects in listener use EffectApplyEvent<YourEffect> or "@First YourEffect"
 */
@JsBinding(JsBinding.Type.CLASS)
public class EffectApplyEvent<T extends IEffect> extends AbstractEffectEvent<T> implements Cancellable {
	protected boolean cancelled;

	public EffectApplyEvent(T effect) {
		super(effect);
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
