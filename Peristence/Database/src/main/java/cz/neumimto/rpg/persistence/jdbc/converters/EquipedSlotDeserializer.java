package cz.neumimto.rpg.persistence.jdbc.converters;

import com.google.gson.*;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.persistance.model.EquipedSlot;

import java.lang.reflect.Type;

/**
 * Created by NeumimTo on 20.5.2018.
 */
public class EquipedSlotDeserializer implements JsonDeserializer<EquipedSlot> {

    @Override
    public EquipedSlot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String className = null;
        int slotId = 0;
        if (object.has("classname")) {
             className = object.get("className").getAsString();
        }
        if (object.has("slotIndex")) {
             slotId = object.get("slotIndex").getAsInt();
        }



        return Rpg.get().getInventoryService().createEquipedSlot(className, slotId);
    }
}
