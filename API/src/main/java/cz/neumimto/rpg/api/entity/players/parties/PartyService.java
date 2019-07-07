package cz.neumimto.rpg.api.entity.players.parties;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.party.IParty;

public interface PartyService<T extends IActiveCharacter> {
    void createNewParty(T leader);

    boolean kickCharacterFromParty(IParty party, T kicked);

    void sendPartyInvite(IParty party, T character);

    void addToParty(IParty party, T character);
}
