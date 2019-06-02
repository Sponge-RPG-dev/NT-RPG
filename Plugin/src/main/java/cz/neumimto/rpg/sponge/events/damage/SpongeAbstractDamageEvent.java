package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.entity.IEntity;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public abstract class SpongeAbstractDamageEvent implements Event {

    private IEntity target;
    private double damage;
    private Cause cause;

    @Override
    public Cause getCause() {
        return cause;
    }

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

    public void setCause(Cause cause) {
        this.cause = cause;
    }
}
