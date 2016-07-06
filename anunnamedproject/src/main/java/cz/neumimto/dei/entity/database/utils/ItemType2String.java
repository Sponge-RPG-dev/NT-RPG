package cz.neumimto.dei.entity.database.utils;

import cz.neumimto.dei.exceptions.NotExistingItemTypeException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import javax.persistence.AttributeConverter;
import java.util.Optional;

/**
 * Created by NeumimTo on 6.7.2016.
 */
public class ItemType2String implements AttributeConverter<ItemType, String> {

    @Override
    public String convertToDatabaseColumn(ItemType attribute) {
        return attribute.getId();
    }

    @Override
    public ItemType convertToEntityAttribute(String s) {
        Optional<ItemType> type = Sponge.getGame().getRegistry().getType(ItemType.class, s);
        if (type.isPresent()) {
            return type.get();
        }
        throw new NotExistingItemTypeException(s);
    }
}
