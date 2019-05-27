package cz.neumimto.rpg.sponge.events.character;


import cz.neumimto.rpg.api.events.character.TargetCharacterEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

abstract class AbstractCharacterEvent implements Event, TargetCharacterEvent {

    private IActiveCharacter target;

    @Override
    public IActiveCharacter getTarget() {
        return target;
    }

    @Override
    public void setTarget(IActiveCharacter target) {
        this.target = target;
    }

    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), target);
    }

}
