package cz.neumimto.rpg.events.damage;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

import java.util.Optional;

/**
 * Called when IEntity gets damaged by normal attack, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link EntityDamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class IEntityWeaponDamageEarlyEvent extends DamageIEntityEarlyEvent {

	private RpgItemStack weapon;

	public IEntityWeaponDamageEarlyEvent(IEntity target, double damage) {
		super(target, damage);
	}

	public IEntityWeaponDamageEarlyEvent(IEntity target, double damage, RpgItemStack rpgItemStack) {
		super(target, damage);
		this.weapon = rpgItemStack;
	}

	public Optional<RpgItemStack> getWeapon() {
		return Optional.ofNullable(weapon);
	}
}
