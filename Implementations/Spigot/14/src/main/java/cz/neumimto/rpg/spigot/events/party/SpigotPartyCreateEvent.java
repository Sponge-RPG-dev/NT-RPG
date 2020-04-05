package cz.neumimto.rpg.spigot.events.party;

import cz.neumimto.rpg.api.events.party.PartyCreateEvent;
import org.bukkit.event.HandlerList;

public class SpigotPartyCreateEvent extends SpigotAbstractPartyEvent implements PartyCreateEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
