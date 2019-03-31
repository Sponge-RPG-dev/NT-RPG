package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 24.6.2017.
 */
public class CharacterInitializedEvent extends AbstractCharacterEvent {

	public CharacterInitializedEvent(IActiveCharacter character) {
		super(character);
	}

}
