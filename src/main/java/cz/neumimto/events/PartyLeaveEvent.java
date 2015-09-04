package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.parties.Party;

/**
 * Created by ja on 2.9.2015.
 */
public class PartyLeaveEvent extends CancellableEvent {
    private Party party;
    private IActiveCharacter leaver;

    public PartyLeaveEvent(Party party, IActiveCharacter leaver) {
        this.party = party;
        this.leaver = leaver;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public IActiveCharacter getLeaver() {
        return leaver;
    }

    public void setLeaver(IActiveCharacter leaver) {
        this.leaver = leaver;
    }
}
