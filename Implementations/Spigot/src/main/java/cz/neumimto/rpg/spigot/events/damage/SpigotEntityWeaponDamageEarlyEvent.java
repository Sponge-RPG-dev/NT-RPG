package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.api.events.damage.IEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.api.items.RpgItemStack;
import org.bukkit.event.HandlerList;

import java.util.Optional;


public class SpigotEntityWeaponDamageEarlyEvent extends SpigotAbstractDamageEvent implements IEntityWeaponDamageEarlyEvent {

    private RpgItemStack weapon;

    @Override
    public Optional<RpgItemStack> getWeapon() {
        return Optional.ofNullable(weapon);
    }

    @Override
    public void setWeapon(RpgItemStack weapon) {
        this.weapon = weapon;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
