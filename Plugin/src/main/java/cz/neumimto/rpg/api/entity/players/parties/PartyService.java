package cz.neumimto.rpg.api.entity.players.parties;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.party.IParty;

public interface PartyService {
    void createNewParty(IActiveCharacter leader);

    boolean kickCharacterFromParty(IParty party, IActiveCharacter kicked);

    void sendPartyInvite(IParty party, IActiveCharacter character);

    void addToParty(IParty party, IActiveCharacter character);
}
