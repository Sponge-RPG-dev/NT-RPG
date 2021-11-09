package cz.neumimto.rpg.persistence.jdbc.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cz.neumimto.rpg.common.model.EquipedSlot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 20.5.2018.
 */

public class EquipedSlot2Json {

    private static Gson gson;

    static {
        gson = new GsonBuilder().registerTypeAdapter(EquipedSlot.class, new EquipedSlotDeserializer()).create();
    }


    public String convertToDatabaseColumn(List vector2is) {
        return gson.toJson(vector2is);
    }


    public List<EquipedSlot> convertToEntityAttribute(String s) {
        if (s == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(s, new TypeToken<List<EquipedSlot>>() {
        }.getType());
    }


}