package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import org.spongepowered.api.event.cause.entity.damage.DamageType;

/**
 * Created by NeumimTo on 8.7.2017.
 */
public class INEntityDamageEvent extends CancellableEvent {

	private IEntity source;
	private IEntity target;
	private double damage;
	private DamageType type;

	public INEntityDamageEvent(IEntity source, IEntity target, double damage, DamageType type) {

		this.source = source;
		this.target = target;
		this.damage = damage;
		this.type = type;
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

	public DamageType getType() {
		return type;
	}

	public void setType(DamageType type) {
		this.type = type;
	}
}
