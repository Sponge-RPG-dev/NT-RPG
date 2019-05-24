package cz.neumimto.rpg.events;

import org.spongepowered.api.event.Cancellable;

public class AbstractCancellableNEvent extends AbstractNEvent implements Cancellable {

    protected boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

}
