package cz.neumimto.rpg.persistance.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import cz.neumimto.rpg.persistance.model.EquipedSlot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by NeumimTo on 20.5.2018.
 */
@Converter(autoApply = true)
public class EquipedSlot2Json implements AttributeConverter<List<EquipedSlot>, String> {
        
    private static Gson gson;

    static {
        gson = new GsonBuilder().registerTypeAdapter(EquipedSlot.class, new Deserializer()).create();
    }

    @Override
    public String convertToDatabaseColumn(List<EquipedSlot> vector2is) {
        return gson.toJson(vector2is);
    }

    @Override
    public List<EquipedSlot> convertToEntityAttribute(String s) {
        if (s == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(s, new TypeToken<List<Integer>>(){}.getType());
    }

    public static class Deserializer implements JsonDeserializer<EquipedSlot> {

        @Override
        public EquipedSlot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String className = object.get("className").getAsString();
            int slotId = object.get("slotIndex").getAsInt();
            try {
                return new EquipedSlot(className, slotId);
            } catch (ClassNotFoundException e) {        
                return null;
            }
        }
    }
}