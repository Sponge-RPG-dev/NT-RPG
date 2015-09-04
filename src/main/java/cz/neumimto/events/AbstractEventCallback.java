package cz.neumimto.events;

import org.spongepowered.api.event.Order;
import org.spongepowered.api.util.event.callback.EventCallback;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public abstract class AbstractEventCallback implements EventCallback {

    @Override
    public boolean isBaseGame() {
        return false;
    }

    @Override
    public Order getOrder() {
        return Order.DEFAULT;
    }

}
