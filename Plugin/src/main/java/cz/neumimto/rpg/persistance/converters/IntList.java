package cz.neumimto.rpg.persistance.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class IntList implements AttributeConverter<List, String> {

    private static Gson gson;

    static {
        gson = new Gson();
    }

    @Override
    public String convertToDatabaseColumn(List vector2is) {
        return gson.toJson(vector2is);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String s) {
        if (s == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(s, new TypeToken<List<Integer>>(){}.getType());
    }
}