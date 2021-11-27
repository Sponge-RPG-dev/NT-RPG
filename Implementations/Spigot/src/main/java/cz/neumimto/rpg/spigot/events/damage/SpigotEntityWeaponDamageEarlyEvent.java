package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.damage.IEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.common.items.RpgItemStack;
import org.bukkit.event.HandlerList;

import java.util.Optional;


public class SpigotEntityWeaponDamageEarlyEvent extends SpigotAbstractDamageEvent implements IEntityWeaponDamageEarlyEvent {

    private RpgItemStack weapon;
    private IEntity damager;

    @Override
    public Optional<RpgItemStack> getWeapon() {
        return Optional.ofNullable(weapon);
    }

    @Override
    public void setWeapon(RpgItemStack weapon) {
        this.weapon = weapon;
    }

    @Override
    public void setDamager(IEntity attacker) {
        this.damager = attacker;
    }

    @Override
    public IEntity getDamager() {
        return damager;
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
