package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.DamageIEntityEarlyEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

/**
 * Called when IEntity gets damaged, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link DamageSource}
 */
public class SpongeDamageIEntityEarlyEvent extends SpongeAbstractDamageEvent implements DamageIEntityEarlyEvent {

}
