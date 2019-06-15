package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.IEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.api.items.RpgItemStack;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

import java.util.Optional;

/**
 * Called when IEntity gets damaged by normal attack, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link EntityDamageSource}
 */
public class SpongeEntityWeaponDamageEarlyEvent extends SpongeAbstractDamageEvent implements IEntityWeaponDamageEarlyEvent, Cancellable {

    private RpgItemStack weapon;
    private boolean cancelled;

    public Optional<RpgItemStack> getWeapon() {
        return Optional.ofNullable(weapon);
    }

    public void setWeapon(RpgItemStack weapon) {
        this.weapon = weapon;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        this.cancelled = state;
    }
}
