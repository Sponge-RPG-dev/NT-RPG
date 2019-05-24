package cz.neumimto.rpg.events.damage;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.entity.AbstractIEntityCancellableEvent;
import cz.neumimto.rpg.common.scripting.JsBinding;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

/**
 * Called when IEntity gets damaged, after damage bonuses of source and resistances of target are applied
 * {@link Cause} contains {@link DamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class DamageIEntityLateEvent extends AbstractIEntityCancellableEvent {
	protected double damage;

	public DamageIEntityLateEvent(IEntity target, double damage) {
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
