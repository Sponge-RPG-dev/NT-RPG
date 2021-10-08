package cz.neumimto.rpg.common.events.character;


import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

/**
 * Base event for when a {@link IActiveCharacter} is a target.
 */
public interface TargetCharacterEvent {

    IActiveCharacter getTarget();

    void setTarget(IActiveCharacter target);

}
