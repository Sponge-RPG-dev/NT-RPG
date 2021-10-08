package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.common.events.damage.IEntityWeaponDamageLateEvent;
import cz.neumimto.rpg.common.items.RpgItemStack;
import org.bukkit.event.HandlerList;

import java.util.Optional;

public class SpigotEntityWeaponDamageLateEvent extends SpigotAbstractDamageEvent implements IEntityWeaponDamageLateEvent {

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
