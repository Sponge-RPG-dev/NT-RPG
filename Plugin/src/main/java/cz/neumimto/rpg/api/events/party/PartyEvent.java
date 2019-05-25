package cz.neumimto.rpg.api.events.party;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.parties.Party;

public interface PartyEvent {
    IActiveCharacter getCharacter();
    Party getParty();
    void setCharacter(IActiveCharacter character);
    void setParty(Party party);
}
