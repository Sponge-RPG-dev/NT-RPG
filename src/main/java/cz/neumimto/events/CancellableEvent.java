package cz.neumimto.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.util.event.callback.CallbackList;


/**
 * Created by NeumimTo on 12.2.2015.
 */
public class CancellableEvent implements Cancellable, Event {

    public boolean cancelled;


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }


    public static CallbackList list = new CallbackList();

    @Override
    public CallbackList getCallbacks() {
        return list;
    }
}
