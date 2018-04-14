package cz.neumimto.rpg.persistance.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Converter
public class IntList implements AttributeConverter<List<Integer>, String> {

    private static Gson gson;

    static {
        gson = new Gson();
    }

    @Override
    public String convertToDatabaseColumn(List<Integer> vector2is) {
        return gson.toJson(vector2is);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String s) {
        return gson.fromJson(s, new TypeToken<List<Integer>>(){}.getType());
    }
}