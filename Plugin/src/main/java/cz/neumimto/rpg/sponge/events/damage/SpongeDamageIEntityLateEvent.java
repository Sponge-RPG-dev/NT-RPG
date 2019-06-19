package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.DamageIEntityLateEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

/**
 * Called when IEntity gets damaged, after damage bonuses of source and resistances of target are applied
 * {@link Cause} contains {@link DamageSource}
 */
public class SpongeDamageIEntityLateEvent extends SpongeAbstractDamageEvent implements DamageIEntityLateEvent {

}
