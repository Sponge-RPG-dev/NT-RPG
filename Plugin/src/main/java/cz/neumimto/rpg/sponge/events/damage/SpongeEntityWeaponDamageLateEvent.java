package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.DamageIEntityEarlyEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

/**
 * Called when IEntity gets damaged by normal attack, after damage bonuses of source and resistances of target are applied
 * {@link Cause} contains {@link EntityDamageSource}
 */
public class SpongeEntityWeaponDamageLateEvent extends SpongeAbstractDamageEvent implements DamageIEntityEarlyEvent {

}
