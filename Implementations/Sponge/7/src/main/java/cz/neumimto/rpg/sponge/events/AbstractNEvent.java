package cz.neumimto.rpg.sponge.events;

import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

public abstract class AbstractNEvent extends AbstractEvent {
    protected Cause cause = Cause.of(EventContext.empty(), SpongeRpgPlugin.getInstance());

    @Override
    public Cause getCause() {
        return cause;
    }

    public void setCause(Cause cause) {
        this.cause = cause;
    }

}
