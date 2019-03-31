package cz.neumimto.rpg.events.entity;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

/**
 * Called when IEntity gets damaged by other IEntity by normal attack
 * {@link Cause} contains {@link EntityDamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class IEntityWeaponDamageEvent extends DamageIEntityEvent {

	public IEntityWeaponDamageEvent(IEntity target, double damage) {
		super(target, damage);
	}

}
