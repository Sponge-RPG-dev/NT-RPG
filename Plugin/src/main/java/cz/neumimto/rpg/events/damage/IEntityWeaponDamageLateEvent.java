package cz.neumimto.rpg.events.damage;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.common.scripting.JsBinding;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

/**
 * Called when IEntity gets damaged by normal attack, after damage bonuses of source and resistances of target are applied
 * {@link Cause} contains {@link EntityDamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class IEntityWeaponDamageLateEvent extends DamageIEntityEarlyEvent {

	public IEntityWeaponDamageLateEvent(IEntity target, double damage) {
		super(target, damage);
	}

}
