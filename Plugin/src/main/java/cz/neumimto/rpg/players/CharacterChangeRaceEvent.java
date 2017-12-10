package cz.neumimto.rpg.players;

import cz.neumimto.rpg.events.CharacterChangeGroupEvent;
import cz.neumimto.rpg.players.groups.Race;

/**
 * Created by NeumimTo on 29.4.2017.
 */
public class CharacterChangeRaceEvent extends CharacterChangeGroupEvent {
	public CharacterChangeRaceEvent(IActiveCharacter character, Race next, Race old) {
		super(next, character, old);
	}

	@Override
	public String toString() {
		return "CharacterChangeRaceEvent{" +
				"Old=" + getOld() +
				"New=" + getNew() +
				"}";
	}
}
