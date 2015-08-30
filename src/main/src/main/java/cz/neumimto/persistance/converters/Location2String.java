package cz.neumimto.persistance.converters;

import org.spongepowered.api.world.Location;

import javax.annotation.Nullable;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by NeumimTo on 25.7.2015.
 */
@Converter
public class Location2String implements AttributeConverter<Location, String> {

    @Override
    public String convertToDatabaseColumn(Location location) {
        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }

    @Override
    public Location convertToEntityAttribute(String s) {
        return null;
    }
}
