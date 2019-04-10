package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
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
        for (Map.Entry<Object, ? extends ConfigurationNode> objectEntry : childrenMap.entrySet()) {
            String propertyName = ((String) objectEntry.getKey()).toLowerCase();
            float f = ((Number) objectEntry.getValue().getValue()).floatValue();
            if (NtRpgPlugin.GlobalScope.spongePropertyService.exists(propertyName)) {
                int idByName = NtRpgPlugin.GlobalScope.spongePropertyService.getIdByName(propertyName);
                map.put(idByName, f);
            } else {
                throw new ObjectMappingException("Unknown property " + propertyName);
            }
        }

        return map;
    }

    @Override
    public void serialize(TypeToken<?> type, @Nullable Map<Integer, Float> obj, ConfigurationNode value) throws ObjectMappingException {

    }

}
