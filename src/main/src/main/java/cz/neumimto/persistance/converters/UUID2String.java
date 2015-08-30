package cz.neumimto.persistance.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

/**
 * Created by NeumimTo on 25.7.2015.
 */
@Converter
public class UUID2String implements AttributeConverter<UUID, String> {
    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String s) {
        return UUID.fromString(s);
    }
}
