package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by NeumimTo on 17.6.2017.
 */
public class INEntityWeaponDamageEvent extends INEntityDamageEvent {

	public INEntityWeaponDamageEvent(IEntity source, IEntity target, double damage) {
		super(source, target, damage, DamageTypes.ATTACK);
	}

}
