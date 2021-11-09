package cz.neumimto.rpg.spigot.events.character;


import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.events.character.TargetCharacterEvent;
import cz.neumimto.rpg.spigot.events.AbstractNEvent;

abstract class AbstractCharacterEvent extends AbstractNEvent implements TargetCharacterEvent {

    private IActiveCharacter target;

    @Override
    public IActiveCharacter getTarget() {
        return target;
    }

    @Override
    public void setTarget(IActiveCharacter target) {
        this.target = target;
    }

}
