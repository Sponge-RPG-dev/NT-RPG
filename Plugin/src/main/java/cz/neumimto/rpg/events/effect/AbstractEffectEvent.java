package cz.neumimto.rpg.events.effect;

import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.events.AbstractNEvent;

public abstract class AbstractEffectEvent extends AbstractNEvent implements TargetIEffectConsumer {
	protected final IEffect effect;

	public AbstractEffectEvent(IEffect effect) {
		this.effect = effect;
	}

	@Override
	public IEffectConsumer getTarget() {
		return effect.getConsumer();
	}

	public IEffect getEffect() {
		return effect;
	}
}
