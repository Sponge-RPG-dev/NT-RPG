package cz.neumimto.rpg.api.events.damage;

import cz.neumimto.rpg.api.items.RpgItemStack;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

import java.util.Optional;

/**
 * Called when IEntity gets damaged by normal attack, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link EntityDamageSource}
 */
public interface IEntityWeaponDamageEarlyEvent extends DamageIEntityEarlyEvent {

    Optional<RpgItemStack> getWeapon();

    void setWeapon(RpgItemStack weapon);
}
