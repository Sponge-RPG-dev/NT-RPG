package cz.neumimto.events;

import cz.neumimto.effects.positive.DodgeEffect;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.events.CancellableEvent;

/**
 * Created by NeumimTo on 6.7.2017.
 */
public class DamageDodgedEvent extends CancellableEvent {
	private final IEntity source;
	private final IEntity target;
	private final IEffectContainer<Float, DodgeEffect> effect;

	public DamageDodgedEvent(IEntity source, IEntity target, IEffectContainer<Float, DodgeEffect> effect) {

		this.source = source;
		this.target = target;
		this.effect = effect;
	}

	public IEntity getSource() {
		return source;
	}

	public IEntity getTarget() {
		return target;
	}

	public IEffectContainer<Float, DodgeEffect> getEffect() {
		return effect;
	}
}
