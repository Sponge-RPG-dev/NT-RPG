package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.logging.Log;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClassExpAdapter implements TypeSerializer<Map<String, Map<EntityType, Double>>> {
    @Override
    public Map<String, Map<EntityType, Double>> deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Map<String, Map<EntityType, Double>> map = new HashMap<>();

        Map<Object, ? extends ConfigurationNode> childrenMap = value.getChildrenMap();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : childrenMap.entrySet()) {
            String dimName = (String) entry.getKey();
            Map<Object, ? extends ConfigurationNode> c = entry.getValue().getChildrenMap();
            Map<EntityType, Double> dimmMap = new HashMap<>();
            for (Map.Entry<Object, ? extends ConfigurationNode> e : c.entrySet()) {
                String s = (String) e.getKey();
                double d = ((Number)e.getValue().getValue()).doubleValue();
                Optional<EntityType> type1 = Sponge.getRegistry().getType(EntityType.class, s.toLowerCase());
                if (type1.isPresent()) {
                    EntityType entityType = type1.get();
                    dimmMap.put(entityType, d);
                } else {
                    Log.warn("Unknown Entity Id " + s.toLowerCase());
                }
            }
            map.put(dimName.toLowerCase(), dimmMap);
        }

        return map;
    }

    @Override
    public void serialize(TypeToken<?> type, Map<String, Map<EntityType, Double>> obj, ConfigurationNode value) throws ObjectMappingException {

    }
}
