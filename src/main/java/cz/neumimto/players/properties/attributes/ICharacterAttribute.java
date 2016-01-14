package cz.neumimto.players.properties.attributes;

import org.spongepowered.api.item.ItemType;

import java.util.Map;

/**
 * Created by NeumimTo on 14.1.16.
 */
public interface ICharacterAttribute {

    String getName();

    void setName(String name);

    Map<Integer,Float> affectsProperties();

    String getDescription();

    void setDescription(String desc);

    ItemType getItemRepresentation();

    void setItemRepresentation(ItemType itemType);

    void setMaxValue(int value);

    int getMaxValue();

    default boolean hasLimit() {
        return getMaxValue() > 0;
    }
}
