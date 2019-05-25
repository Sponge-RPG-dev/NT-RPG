package cz.neumimto.rpg.common.persistance.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cz.neumimto.rpg.common.persistance.model.EquipedSlot;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 20.5.2018.
 */
@Converter(autoApply = true)
public class EquipedSlot2Json implements AttributeConverter<List, String> {

    private static Gson gson;

    static {
        gson = new GsonBuilder().registerTypeAdapter(EquipedSlot.class, new EquipedSlotDeserializer()).create();
    }

    @Override
    public String convertToDatabaseColumn(List vector2is) {
        return gson.toJson(vector2is);
    }

    @Override
    public List<EquipedSlot> convertToEntityAttribute(String s) {
        if (s == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(s, new TypeToken<List<EquipedSlot>>() {
        }.getType());
    }


}