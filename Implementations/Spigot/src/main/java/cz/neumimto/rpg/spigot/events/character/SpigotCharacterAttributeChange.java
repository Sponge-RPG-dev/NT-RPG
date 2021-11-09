package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.events.character.CharacterAttributeChange;
import org.bukkit.event.HandlerList;

import java.util.Map;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public class SpigotCharacterAttributeChange extends AbstractCharacterEvent implements CharacterAttributeChange {

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

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
