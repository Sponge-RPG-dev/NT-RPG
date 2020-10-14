package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.api.events.damage.DamageIEntityEarlyEvent;
import org.bukkit.event.HandlerList;


public class SpigotDamageIEntityEarlyEvent extends SpigotAbstractDamageEvent implements DamageIEntityEarlyEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
