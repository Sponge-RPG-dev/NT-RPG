package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.parties.Party;

/**
 * Created by ja on 2.9.2015.
 */
public class PartyKickEvent extends PartyLeaveEvent {

    public PartyKickEvent(Party party, IActiveCharacter kicked) {
        super(party, kicked);
    }

}
