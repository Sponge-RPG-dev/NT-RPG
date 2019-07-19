package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.api.events.damage.DamageIEntityEarlyEvent;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;


public class SpigotEntityProjectileDamageEarlyEvent extends SpigotAbstractDamageEvent implements DamageIEntityEarlyEvent {

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
}
