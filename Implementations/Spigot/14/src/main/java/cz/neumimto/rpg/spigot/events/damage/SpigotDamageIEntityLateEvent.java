package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.api.events.damage.DamageIEntityLateEvent;
import org.bukkit.event.HandlerList;

public class SpigotDamageIEntityLateEvent extends SpigotAbstractDamageEvent implements DamageIEntityLateEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
