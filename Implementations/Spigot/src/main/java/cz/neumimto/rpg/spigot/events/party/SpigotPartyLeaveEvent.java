package cz.neumimto.rpg.spigot.events.party;

import cz.neumimto.rpg.api.events.party.PartyLeaveEvent;
import org.bukkit.event.HandlerList;

public class SpigotPartyLeaveEvent extends SpigotAbstractPartyEvent implements PartyLeaveEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
