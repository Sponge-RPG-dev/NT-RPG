package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by ja on 17.6.2017.
 */
public class INEntityWeaponDamageEvent extends CancellableEvent {
	private IEntity source;
	private IEntity target;
	private double damage;

	public INEntityWeaponDamageEvent(IEntity source, IEntity target, double damage) {
		this.source = source;
		this.target = target;
		this.damage = damage;
	}

	public INEntityWeaponDamageEvent(IEntity source, IEntity target) {
		this.source = source;
		this.target = target;
	}

	public IEntity getSource() {
		return source;
	}

	public void setSource(IEntity source) {
		this.source = source;
	}

	public IEntity getTarget() {
		return target;
	}

	public void setTarget(IEntity target) {
		this.target = target;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}
}
