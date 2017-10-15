package cz.neumimto.dei.entity.database.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

/**
 * Created by ja on 5.7.2016.
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
