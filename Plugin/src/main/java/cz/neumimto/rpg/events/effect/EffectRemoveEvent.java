package cz.neumimto.rpg.events.effect;

import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when an {@link IEffect} is removed from {@link IEffectConsumer}.
 * Contains {@link IEffect} in {@link Cause}
 *
 * To filter specific effects in listener use "@First YourEffect"
 */
@JsBinding(JsBinding.Type.CLASS)
public class EffectRemoveEvent extends AbstractEffectEvent {
	public EffectRemoveEvent(IEffect effect) {
		super(effect);
	}
}
