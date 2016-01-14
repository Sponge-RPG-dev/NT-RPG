package cz.neumimto.players.properties.attributes;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fs on 14.1.16.
 */
public abstract class CharacterAttribute implements ICharacterAttribute {

    private String name;

    private Map<Integer, Float> affectedProperties = new HashMap<>();
    private String desc;
    private ItemType item = ItemTypes.STONE;
    private int maxval;
    private int maxvalue;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<Integer, Float> affectsProperties() {
        return affectedProperties;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public void setDescription(String desc) {
        this.desc = desc;
    }

    @Override
    public ItemType getItemRepresentation() {
        return item;
    }

    @Override
    public void setItemRepresentation(ItemType itemType) {
        this.item = itemType;
    }

    @Override
    public void setMaxValue(int value) {
        this.maxval = value;
    }

    @Override
    public int getMaxValue() {
        return maxvalue;
    }
}
