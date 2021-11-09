package cz.neumimto.rpg.common.events.party;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.events.Cancellable;

public interface PartyEvent extends Cancellable {

    IActiveCharacter getCharacter();

    IParty getParty();

    void setCharacter(IActiveCharacter character);

    void setParty(IParty party);

}
