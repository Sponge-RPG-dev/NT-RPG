package cz.neumimto.rpg.events.party;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.parties.Party;

public class PartyLeaveEvent extends AbstractPartyEvent {
	public PartyLeaveEvent(IActiveCharacter character, Party party) {
		super(character, party);
	}
}
