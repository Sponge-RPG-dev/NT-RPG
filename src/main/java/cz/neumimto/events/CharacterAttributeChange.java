package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public class CharacterAttributeChange extends CharacterEvent {
    private final int attributechange;

    public CharacterAttributeChange(IActiveCharacter character, int attributechange) {
        super(character);
        this.attributechange = attributechange;
    }


    public int getAttributechange() {
        return attributechange;
    }
}
