package cz.neumimto.listeners;

import cz.neumimto.ResourceLoader;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 5.3.2016.
 */
@ResourceLoader.ListenerClass
public class JSListener {

    private Set<Consumer<DamageEntityEvent>> damageEntityEvents = new HashSet<>();

    @Listener(order = Order.BEFORE_POST)
    public void onEventDamage(DamageEntityEvent event) {
        for (Consumer<DamageEntityEvent> it : damageEntityEvents) {
            it.accept(event);
        }
    }

    private Set<Consumer<DisplaceEntityEvent>> displaceEntityEvents = new HashSet<>();

    @Listener(order = Order.BEFORE_POST)
    public void onEventDamage(DisplaceEntityEvent event) {
        for (Consumer<DisplaceEntityEvent> it : displaceEntityEvents) {
            it.accept(event);
        }
    }
}
