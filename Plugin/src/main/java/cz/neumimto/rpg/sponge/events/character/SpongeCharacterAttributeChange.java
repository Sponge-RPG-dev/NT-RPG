package cz.neumimto.rpg.sponge.events.character;

import cz.neumimto.rpg.api.events.character.CharacterAttributeChange;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public class SpongeCharacterAttributeChange extends AbstractCharacterEvent implements CharacterAttributeChange {

    private int attributeChange;
    private AttributeConfig attribute;

    public void setAttributeChange(int attributeChange) {
        this.attributeChange = attributeChange;
    }

    public AttributeConfig getAttribute() {
        return attribute;
    }

    @Override
    public int getAttributeChange() {
        return attributeChange;
    }

    @Override
    public void setAttribute(AttributeConfig attribute) {
        this.attribute = attribute;
    }
}