package cz.neumimto.rpg.api.events.party;

import cz.neumimto.rpg.api.events.Cancellable;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.parties.Party;

public interface PartyEvent extends Cancellable {
    IActiveCharacter getCharacter();
    Party getParty();
    void setCharacter(IActiveCharacter character);
    void setParty(Party party);
}
