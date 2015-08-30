package cz.neumimto.players.events;

import cz.neumimto.events.CancellableEvent;
import cz.neumimto.players.ActiveCharacter;
import cz.neumimto.players.parties.Party;

/**
 * Created by NeumimTo on 11.8.2015.
 */
public class PartyInviteEvent extends CancellableEvent {
    private final Party party;
    private ActiveCharacter character;

    public PartyInviteEvent(Party party, ActiveCharacter character) {

        this.party = party;
        this.character = character;
    }

    public Party getParty() {
        return party;
    }

    public ActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(ActiveCharacter character) {
        this.character = character;
    }
}
