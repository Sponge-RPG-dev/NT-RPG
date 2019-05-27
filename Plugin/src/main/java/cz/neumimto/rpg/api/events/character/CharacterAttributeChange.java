package cz.neumimto.rpg.api.events.character;

import cz.neumimto.rpg.players.attributes.Attribute;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public interface CharacterAttributeChange extends TargetCharacterEvent {

    int getAttributeChange();

    void setAttributeChange(int c);

    void setAttribute(Attribute attribute);

    Attribute getAttribute();
}
