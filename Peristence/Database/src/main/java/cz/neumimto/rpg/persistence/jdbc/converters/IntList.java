package cz.neumimto.rpg.persistence.jdbc.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class IntList {

    private static Gson gson;

    static {
        gson = new Gson();
    }


    public String convertToDatabaseColumn(List vector2is) {
        return gson.toJson(vector2is);
    }


    public List<Integer> convertToEntityAttribute(String s) {
        if (s == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(s, new TypeToken<List<Integer>>() {
        }.getType());
    }
}