package cz.neumimto.rpg.sponge.events.party;

import cz.neumimto.rpg.api.events.party.PartyEvent;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.players.parties.Party;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

public class AbstractPartyEvent implements PartyEvent, Event {

    private Party party;
    private IActiveCharacter character;
    private boolean cancelled;

    @Override
    public Party getParty() {
        return party;
    }

    @Override
    public void setParty(Party party) {
        this.party = party;
    }

    @Override
    public IActiveCharacter getCharacter() {
        return character;
    }

    @Override
    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), character);
    }
}
