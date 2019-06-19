package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.DamageIEntityEarlyEvent;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;

/**
 * Called when IEntity gets damaged by projectile, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link IndirectEntityDamageSource}
 */
public class SpongeEntityProjectileDamageEarlyEvent extends SpongeAbstractDamageEvent implements DamageIEntityEarlyEvent {

    private Projectile projectile;

    public Projectile getProjectile() {
        return projectile;
    }

    public void setProjectile(Projectile projectile) {
        this.projectile = projectile;
    }

}
