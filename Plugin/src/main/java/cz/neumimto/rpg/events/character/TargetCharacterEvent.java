package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.events.entity.TargetIEntityEvent;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Base event for when a {@link IActiveCharacter} is a target.
 */
public interface TargetCharacterEvent extends TargetIEntityEvent {
	@Override
	IActiveCharacter getTarget();
}
