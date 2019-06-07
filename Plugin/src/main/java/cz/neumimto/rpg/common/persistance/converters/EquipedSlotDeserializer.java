package cz.neumimto.rpg.common.persistance.converters;

import com.google.gson.*;
import cz.neumimto.rpg.api.persistance.model.EquipedSlot;

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
