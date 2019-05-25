package cz.neumimto.rpg.api.events.damage;

import cz.neumimto.rpg.api.events.entity.TargetIEntityEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

/**
 * Called when IEntity gets damaged, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link DamageSource}
 */
public interface DamageIEntityEarlyEvent extends TargetIEntityEvent {

    double getDamage();

    void setDamage(double damage);

}
