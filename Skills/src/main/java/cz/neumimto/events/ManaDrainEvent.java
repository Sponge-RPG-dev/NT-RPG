package cz.neumimto.events;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.events.CancellableEvent;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 7.7.2017.
 */
public class ManaDrainEvent extends CancellableEvent {
	private final IEntity source;
	private final IActiveCharacter target;
	private double amountDrained;

	public ManaDrainEvent(IEntity source, IActiveCharacter target, double k) {

		this.source = source;
		this.target = target;
		this.amountDrained = k;
	}

	public IEntity getSource() {
		return source;
	}

	public IActiveCharacter getTarget() {
		return target;
	}

	public double getAmountDrained() {
		return amountDrained;
	}

	public void setAmountDrained(double amountDrained) {
		this.amountDrained = amountDrained;
	}
}
