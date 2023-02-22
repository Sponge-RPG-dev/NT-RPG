package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.damage.DamageIEntityEarlyEvent;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;


public class SpigotEntityProjectileDamageEarlyEvent extends SpigotAbstractDamageEvent implements DamageIEntityEarlyEvent {

    private Projectile projectile;
    private IEntity damager;

    public Projectile getProjectile() {
        return projectile;
    }

    public void setProjectile(Projectile projectile) {
        this.projectile = projectile;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public void setDamager(IEntity damager) {
        this.damager = damager;
    }

    public IEntity getDamager() {
        return damager;
    }
}
