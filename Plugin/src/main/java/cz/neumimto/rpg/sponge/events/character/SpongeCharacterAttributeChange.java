package cz.neumimto.rpg.sponge.events.character;

import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.events.character.CharacterAttributeChange;

import java.util.Map;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public class SpongeCharacterAttributeChange extends AbstractCharacterEvent implements CharacterAttributeChange {

    private int attributeChange;
    private Map<AttributeConfig, Integer> attribute;

    @Override
    public void setAttributeChange(int attributeChange) {
        this.attributeChange = attributeChange;
    }

    @Override
    public Map<AttributeConfig, Integer> getAttribute() {
        return attribute;
    }

    @Override
    public int getAttributeChange() {
        return attributeChange;
    }

    @Override
    public void setAttribute(Map<AttributeConfig, Integer> attribute) {
        this.attribute = attribute;
    }

}
