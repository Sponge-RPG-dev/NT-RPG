package cz.neumimto.rpg.api.events.character;

import cz.neumimto.rpg.players.attributes.Attribute;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public interface CharacterAttributeChange extends TargetCharacterEvent {

    int getAttributeChange();

    void setAttributeChange();

    void setAttribute(Attribute attribute);

    void getAttribute(Attribute attribute);
}
