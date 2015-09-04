package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.parties.Party;

/**
 * Created by NeumimTo on 11.8.2015.
 */
public class PartyInviteEvent extends CancellableEvent {
    private final Party party;
    private IActiveCharacter character;

    public PartyInviteEvent(Party party, IActiveCharacter character) {

        this.party = party;
        this.character = character;
    }

    public Party getParty() {
        return party;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }
}
