package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.parties.Party;

/**
 * Created by ja on 2.9.2015.
 */
public class PartyJoinEvent extends CancellableEvent {
    private final IActiveCharacter character;
    private final Party party;

    public PartyJoinEvent(IActiveCharacter character, Party party) {
        this.character = character;
        this.party = party;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public Party getParty() {
        return party;
    }
}
