package cz.neumimto.rpg.events.party;

import cz.neumimto.rpg.events.character.AbstractCharacterCancellableEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.parties.Party;

public abstract class AbstractPartyEvent extends AbstractCharacterCancellableEvent {

    protected final Party party;

    public AbstractPartyEvent(IActiveCharacter character, Party party) {
        super(character);
        this.party = party;
    }

    public Party getParty() {
        return party;
    }

}
