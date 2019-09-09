package cz.neumimto.rpg.spigot.events.party;

import cz.neumimto.rpg.api.events.party.PartyJoinEvent;
import org.bukkit.event.HandlerList;

public class SpigotPartyJoinEvent extends SpigotAbstractPartyEvent implements PartyJoinEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
