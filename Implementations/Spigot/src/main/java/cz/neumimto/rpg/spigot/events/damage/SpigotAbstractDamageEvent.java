package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.spigot.events.AbstractNEvent;
import org.bukkit.event.Cancellable;

public abstract class SpigotAbstractDamageEvent extends AbstractNEvent implements Cancellable {

    private IEntity target;
    private double damage;
    private boolean cancelled;

    public IEntity getTarget() {
        return target;
    }

    public void setTarget(IEntity target) {
        this.target = target;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
