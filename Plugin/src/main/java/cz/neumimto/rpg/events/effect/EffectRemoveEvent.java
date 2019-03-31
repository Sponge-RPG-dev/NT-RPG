package cz.neumimto.rpg.events.effect;

import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;

/**
 * Called when an {@link IEffect} expires from {@link IEffectConsumer}.
 */
public class EffectRemoveEvent extends AbstractEffectEvent {
	public EffectRemoveEvent(IEffect effect) {
		super(effect);
	}
}
