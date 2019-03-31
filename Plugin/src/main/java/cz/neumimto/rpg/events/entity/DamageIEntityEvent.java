package cz.neumimto.rpg.events.entity;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

/**
 * Called when IEntity somehow gets damaged
 * {@link Cause} contains {@link DamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class DamageIEntityEvent extends AbstractIEntityCancellableEvent {
	protected double damage;

	public DamageIEntityEvent(IEntity target, double damage) {
		super(target);
		this.damage = damage;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

}
