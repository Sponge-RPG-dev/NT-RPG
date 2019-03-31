package cz.neumimto.events;

import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.events.AbstractCancellableNEvent;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 5.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class StunApplyAttemptEvent extends AbstractCancellableNEvent {
	private final StunEffect effect;
	private final IEffectConsumer caster;
	private final IEffectConsumer target;

	public StunApplyAttemptEvent(IEffectConsumer caster, StunEffect effect) {
		this.effect = effect;
		this.caster = caster;
		this.target = effect.getConsumer();
	}

	public StunEffect getEffect() {
		return effect;
	}

	public IEffectConsumer getCaster() {
		return caster;
	}

	public IEffectConsumer getTarget() {
		return target;
	}
}
