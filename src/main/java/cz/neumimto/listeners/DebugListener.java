package cz.neumimto.listeners;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Texts;

import java.util.Optional;

/**
 * Created by NeumimTo on 22.12.2015.
 */
public class DebugListener {

    @Listener(order = Order.LAST)
    public void debug(DamageEntityEvent event) {
        final Cause cause = event.getCause();
        Optional<EntityDamageSource> first = cause.first(EntityDamageSource.class);
        if (first.isPresent()) {
            Entity targetEntity = event.getTargetEntity();
            EntityDamageSource entityDamageSource = first.get();
            Entity source = entityDamageSource.getSource();
            if (source.getType() == EntityTypes.PLAYER) {
                ((Player)source).sendMessage(Texts.of(">> " + event.getFinalDamage()));
            }
            if (targetEntity.getType() == EntityTypes.PLAYER) {
                ((Player)targetEntity).sendMessage(Texts.of("<< " + event.getFinalDamage()));
            }
        }
    }
}
