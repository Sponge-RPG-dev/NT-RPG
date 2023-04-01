package cz.neumimto.rpg.spigot.events.character;


import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.events.character.TargetCharacterEvent;
import cz.neumimto.rpg.spigot.events.AbstractNEvent;

public abstract class AbstractCharacterEvent extends AbstractNEvent implements TargetCharacterEvent {

    private ActiveCharacter target;

    @Override
    public ActiveCharacter getTarget() {
        return target;
    }

    @Override
    public void setTarget(ActiveCharacter target) {
        this.target = target;
    }

}
