package cz.neumimto.rpg.sponge.events;

import org.spongepowered.api.event.Cancellable;

public class CancellableNEvent extends AbstractNEvent implements Cancellable {
    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
