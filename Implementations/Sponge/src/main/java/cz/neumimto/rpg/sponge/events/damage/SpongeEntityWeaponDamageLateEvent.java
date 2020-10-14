package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.IEntityWeaponDamageLateEvent;
import cz.neumimto.rpg.api.items.RpgItemStack;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

import java.util.Optional;

/**
 * Called when IEntity gets damaged by normal attack, after damage bonuses of source and resistances of target are applied
 * {@link Cause} contains {@link EntityDamageSource}
 */
public class SpongeEntityWeaponDamageLateEvent extends SpongeAbstractDamageEvent implements IEntityWeaponDamageLateEvent {

    private RpgItemStack weapon;

    @Override
    public Optional<RpgItemStack> getWeapon() {
        return Optional.ofNullable(weapon);
    }

    @Override
    public void setWeapon(RpgItemStack weapon) {
        this.weapon = weapon;
    }

}
