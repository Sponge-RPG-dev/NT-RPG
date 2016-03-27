package cz.neumimto.rpg.events;

import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 13.3.2015.
 */
public class CharacterEvent extends CancellableEvent {
    private IActiveCharacter IActiveCharacter;

    public CharacterEvent(IActiveCharacter IActiveCharacter) {
        this.IActiveCharacter = IActiveCharacter;
    }

    public IActiveCharacter getActiveCharacter() {
        return IActiveCharacter;
    }

    public void setActiveCharacter(IActiveCharacter IActiveCharacter) {
        this.IActiveCharacter = IActiveCharacter;
    }
}
