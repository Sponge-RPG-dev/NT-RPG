package cz.neumimto.rpg.sponge.events.character;

import cz.neumimto.rpg.api.events.character.CharacterAttributeChange;
import cz.neumimto.rpg.players.attributes.Attribute;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public class SpongeCharacterAttributeChange extends AbstractCharacterEvent implements CharacterAttributeChange {

    private int attributeChange;
    private Attribute attribute;

    public void setAttributeChange(int attributeChange) {
        this.attributeChange = attributeChange;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public int getAttributeChange() {
        return attributeChange;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
