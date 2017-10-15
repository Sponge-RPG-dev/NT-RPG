package cz.neumimto.rpg.persistance.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.AttributeConverter;
import java.util.Map;

/**
 * Created by NeumimTo on 8.10.2016.
 */
public class MapSL2Json implements AttributeConverter<Object, String> {

	private static Gson gson;

	static {
		gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
	}

	@Override
	public String convertToDatabaseColumn(Object data) {
		return gson.toJson(data, Map.class);
	}

	@Override
	public Object convertToEntityAttribute(String s) {
		return gson.fromJson(s, Map.class);
	}
}
