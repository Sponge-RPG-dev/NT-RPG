package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.common.events.damage.DamageIEntityLateEvent;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;

public class SpigotEntityProjectileDamageLateEvent extends SpigotAbstractDamageEvent implements DamageIEntityLateEvent {

    private Projectile projectile;

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
}
