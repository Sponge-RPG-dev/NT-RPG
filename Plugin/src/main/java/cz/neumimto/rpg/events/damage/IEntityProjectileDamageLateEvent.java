package cz.neumimto.rpg.events.damage;

import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.entities.IEntity;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;

/**
 * Called when IEntity gets damaged by projectile, after damage bonuses of source and resistances of target are applied
 * {@link Cause} contains {@link IndirectEntityDamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class IEntityProjectileDamageLateEvent extends DamageIEntityEarlyEvent {
    private final Projectile projectile;

    public IEntityProjectileDamageLateEvent(IEntity target, double projectileDamage, Projectile projectile) {
        super(target, projectileDamage);
        this.projectile = projectile;
    }

    public Projectile getProjectile() {
        return projectile;
    }
}
