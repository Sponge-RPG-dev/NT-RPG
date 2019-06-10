package cz.neumimto.rpg.sponge.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.warn;

/**
 * Created by NeumimTo on 11.3.2019.
 */
public class AttributeMapAdapter implements TypeSerializer<Map<AttributeConfig, Integer>> {

    @Override
    public Map<AttributeConfig, Integer> deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Map<Object, ? extends ConfigurationNode> childrenMap = value.getChildrenMap();
        Map<AttributeConfig, Integer> map = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : childrenMap.entrySet()) {
            String key = (String) entry.getKey();

            Optional<AttributeConfig> type1 = Rpg.get().getPropertyService().getAttributeById(key);
            if (type1.isPresent()) {
                AttributeConfig attribute = type1.get();
                int anInt = entry.getValue().getInt();
                map.put(attribute, anInt);
            } else {
                warn("Unknown attribute " + key + ". Should be one of: " +
                        Rpg.get().getPropertyService().getAttributes().keySet().stream()
                                .collect(Collectors.joining(", "))
                );
            }
        }
        return map;
    }

    @Override
    public void serialize(TypeToken<?> type, @Nullable Map<AttributeConfig, Integer> obj, ConfigurationNode value) throws ObjectMappingException {

    }
}
