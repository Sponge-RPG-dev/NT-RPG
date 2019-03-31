package cz.neumimto.rpg.events.effect;

import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when an {@link IEffect} is removed from {@link IEffectConsumer}.
 * Contains {@link IEffect} in {@link Cause}
 *
 * To filter specific effects in listener use EffectRemoveEvent<YourEffect> or "@First YourEffect"
 */
@JsBinding(JsBinding.Type.CLASS)
public class EffectRemoveEvent<T extends IEffect> extends AbstractEffectEvent<T> {
	public EffectRemoveEvent(T effect) {
		super(effect);
	}
}
