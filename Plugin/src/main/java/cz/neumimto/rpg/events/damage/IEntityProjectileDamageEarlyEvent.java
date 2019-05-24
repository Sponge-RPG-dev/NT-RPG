package cz.neumimto.rpg.events.damage;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.common.scripting.JsBinding;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;

/**
 * Called when IEntity gets damaged by projectile, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link IndirectEntityDamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class IEntityProjectileDamageEarlyEvent extends DamageIEntityEarlyEvent {
	private final Projectile projectile;

	public IEntityProjectileDamageEarlyEvent(IEntity target, double projectileDamage, Projectile projectile) {
		super(target, projectileDamage);
		this.projectile = projectile;
	}

	public Projectile getProjectile() {
		return projectile;
	}
}
