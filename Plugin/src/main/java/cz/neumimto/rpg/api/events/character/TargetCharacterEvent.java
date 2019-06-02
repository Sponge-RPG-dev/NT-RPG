package cz.neumimto.rpg.api.events.character;


import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

/**
 * Base event for when a {@link IActiveCharacter} is a target.
 */
public interface TargetCharacterEvent {

    IActiveCharacter getTarget();

    void setTarget(IActiveCharacter target);
}
