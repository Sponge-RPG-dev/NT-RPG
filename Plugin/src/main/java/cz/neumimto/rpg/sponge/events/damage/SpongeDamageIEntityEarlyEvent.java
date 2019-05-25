package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.DamageIEntityEarlyEvent;
import cz.neumimto.rpg.api.events.entity.TargetIEntityEvent;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.IEntity;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

/**
 * Called when IEntity gets damaged, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link DamageSource}
 */
public class SpongeDamageIEntityEarlyEvent extends SpongeAbstractDamageEvent implements DamageIEntityEarlyEvent {

}
