package cz.neumimto.rpg.api.events.character;

import cz.neumimto.rpg.api.configuration.AttributeConfig;

import java.util.Map;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public interface CharacterAttributeChange extends TargetCharacterEvent {

    int getAttributeChange();

    void setAttributeChange(int c);

    void setAttribute(Map<AttributeConfig, Integer> attributes);

    Map<AttributeConfig, Integer> getAttribute();

}
