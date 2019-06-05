package cz.neumimto.rpg.sponge.events.party;

import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.api.events.party.PartyEvent;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

public class AbstractPartyEvent implements PartyEvent, Event {

    private IParty party;
    private IActiveCharacter character;
    private boolean cancelled;

    @Override
    public IParty getParty() {
        return party;
    }

    @Override
    public void setParty(IParty party) {
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
