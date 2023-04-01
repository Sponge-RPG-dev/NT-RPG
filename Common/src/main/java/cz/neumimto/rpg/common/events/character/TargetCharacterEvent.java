package cz.neumimto.rpg.common.events.character;


import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

/**
 * Base event for when a {@link ActiveCharacter} is a target.
 */
public interface TargetCharacterEvent {

    ActiveCharacter getTarget();

    void setTarget(ActiveCharacter target);

}
