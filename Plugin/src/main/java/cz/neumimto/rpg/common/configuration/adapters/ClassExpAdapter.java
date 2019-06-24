package cz.neumimto.rpg.common.configuration.adapters;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.HashMap;
import java.util.Map;

public class ClassExpAdapter implements TypeSerializer<Map<String, Map<String, Double>>> {
    @Override
    public Map<String, Map<String, Double>> deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Map<String, Map<String, Double>> map = new HashMap<>();

        Map<Object, ? extends ConfigurationNode> childrenMap = value.getChildrenMap();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : childrenMap.entrySet()) {
            String dimName = (String) entry.getKey();
            Map<Object, ? extends ConfigurationNode> c = entry.getValue().getChildrenMap();
            Map<String, Double> dimmMap = new HashMap<>();
            for (Map.Entry<Object, ? extends ConfigurationNode> e : c.entrySet()) {
                String s = (String) e.getKey();
                double d = ((Number) e.getValue().getValue()).doubleValue();
                dimmMap.put(s, d);
            }
            map.put(dimName.toLowerCase(), dimmMap);
        }
        return map;
    }

    @Override
    public void serialize(TypeToken<?> type, Map<String, Map<String, Double>> obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(obj);
    }
}
