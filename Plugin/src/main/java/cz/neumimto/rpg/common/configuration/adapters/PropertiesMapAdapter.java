package cz.neumimto.rpg.common.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.logging.Log;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PropertiesMapAdapter implements TypeSerializer<Map<Integer, Float>> {

    @Override
    public Map<Integer, Float> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
        Map<Integer, Float> map = new HashMap<>();

        Map<Object, ? extends ConfigurationNode> childrenMap = configurationNode.getChildrenMap();
        PropertyService propertyService = Rpg.get().getPropertyService();
        for (Map.Entry<Object, ? extends ConfigurationNode> objectEntry : childrenMap.entrySet()) {
            String propertyName = ((String) objectEntry.getKey()).toLowerCase();
            float f = ((Number) objectEntry.getValue().getValue()).floatValue();
            if (propertyService.exists(propertyName)) {
                int idByName = propertyService.getIdByName(propertyName);
                map.put(idByName, f);
            } else {
                Log.warn("Unknown property " + propertyName);
            }
        }

        return map;
    }

    @Override
    public void serialize(TypeToken<?> type, @Nullable Map<Integer, Float> obj, ConfigurationNode value) throws ObjectMappingException {
        if (obj == null) {
            return;
        }
        Map<String, Float> floatMap = new HashMap<>();
        PropertyService propertyService = Rpg.get().getPropertyService();

        for (Map.Entry<Integer, Float> integerFloatEntry : obj.entrySet()) {
            Integer key = integerFloatEntry.getKey();
            String nameById = propertyService.getNameById(key);
            if (nameById == null) {
                continue;
            }
            floatMap.put(nameById, integerFloatEntry.getValue());
        }
        value.setValue(floatMap);
    }

}
