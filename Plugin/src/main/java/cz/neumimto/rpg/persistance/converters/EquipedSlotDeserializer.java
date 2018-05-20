package cz.neumimto.rpg.persistance.converters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import cz.neumimto.rpg.persistance.model.EquipedSlot;

import java.lang.reflect.Type;

/**
 * Created by NeumimTo on 20.5.2018.
 */
public class EquipedSlotDeserializer implements JsonDeserializer<EquipedSlot> {

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
