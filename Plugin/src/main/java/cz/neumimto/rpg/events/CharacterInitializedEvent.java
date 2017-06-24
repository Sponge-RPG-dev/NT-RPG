package cz.neumimto.rpg.events;

import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

/**
 * Created by NeumimTo on 24.6.2017.
 */
public class CharacterInitializedEvent implements Event {
	private final IActiveCharacter character;

	public CharacterInitializedEvent(IActiveCharacter character) {
		this.character = character;
	}

	public IActiveCharacter getCharacter() {
		return character;
	}

	@Override
	public Cause getCause() {
		return null;
	}
}
