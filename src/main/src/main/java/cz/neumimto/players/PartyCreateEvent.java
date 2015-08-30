package cz.neumimto.players;

import cz.neumimto.events.CancellableEvent;

/**
 * Created by NeumimTo on 11.8.2015.
 */
public class PartyCreateEvent extends CancellableEvent {
    private ActiveCharacter character;

    public PartyCreateEvent(ActiveCharacter character) {
        super();
        this.character = character;
    }

    public ActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(ActiveCharacter character) {
        this.character = character;
    }
}
