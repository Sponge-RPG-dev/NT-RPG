package cz.neumimto.rpg.common.events.party;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.events.Cancellable;

public interface PartyEvent extends Cancellable {

    ActiveCharacter getCharacter();

    IParty getParty();

    void setCharacter(ActiveCharacter character);

    void setParty(IParty party);

}
