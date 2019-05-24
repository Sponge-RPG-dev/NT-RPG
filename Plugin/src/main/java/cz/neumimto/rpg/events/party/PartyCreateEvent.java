package cz.neumimto.rpg.events.party;

import cz.neumimto.rpg.events.character.AbstractCharacterCancellableEvent;
import cz.neumimto.rpg.players.IActiveCharacter;

public class PartyCreateEvent extends AbstractCharacterCancellableEvent {
    public PartyCreateEvent(IActiveCharacter character) {
        super(character);
    }
}
