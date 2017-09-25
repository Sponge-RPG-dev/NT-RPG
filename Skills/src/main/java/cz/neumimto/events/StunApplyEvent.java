package cz.neumimto.events;

import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.events.CancellableEvent;

/**
 * Created by NeumimTo on 5.7.2017.
 */
public class StunApplyEvent extends CancellableEvent {

	private final IEffectConsumer source;
	private final IEffectConsumer target;
	private final StunEffect effect;

	public StunApplyEvent(IEffectConsumer source, IEffectConsumer target, StunEffect effect) {
		this.source = source;
		this.target = target;
		this.effect = effect;
	}

	public IEffectConsumer getSource() {
		return source;
	}

	public IEffectConsumer getTarget() {
		return target;
	}

	public StunEffect getEffect() {
		return effect;
	}
}
