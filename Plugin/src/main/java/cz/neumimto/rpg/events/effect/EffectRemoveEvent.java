package cz.neumimto.rpg.events.effect;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.effects.IEffectConsumer;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when an {@link IEffect} is removed from {@link IEffectConsumer}.
 * Contains {@link IEffect} in {@link Cause}
 * <p>
 * To filter specific effects in listener use EffectRemoveEvent<YourEffect> or "@First YourEffect"
 */
@JsBinding(JsBinding.Type.CLASS)
public class EffectRemoveEvent<T extends IEffect> extends AbstractEffectEvent<T> {
    public EffectRemoveEvent(T effect) {
        super(effect);
    }
}
