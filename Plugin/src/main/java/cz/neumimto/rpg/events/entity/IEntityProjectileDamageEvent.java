package cz.neumimto.rpg.events.entity;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.entity.projectile.Projectile;

/**
 * Created by NeumimTo on 17.6.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class IEntityProjectileDamageEvent extends DamageIEntityEvent {
	private final Projectile projectile;

	public IEntityProjectileDamageEvent(IEntity target, double projectileDamage, Projectile projectile) {
		super(target, projectileDamage);
		this.projectile = projectile;
	}

	public Projectile getProjectile() {
		return projectile;
	}
}
