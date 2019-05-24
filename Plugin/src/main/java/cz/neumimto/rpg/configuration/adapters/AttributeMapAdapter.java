package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.players.attributes.Attribute;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.warn;

/**
 * Created by NeumimTo on 11.3.2019.
 */
public class AttributeMapAdapter implements TypeSerializer<Map<Attribute, Integer>> {

    @Override
    public Map<Attribute, Integer> deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Map<Object, ? extends ConfigurationNode> childrenMap = value.getChildrenMap();
        Map<Attribute, Integer> map = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : childrenMap.entrySet()) {
            String key = (String) entry.getKey();

            Optional<Attribute> type1 = Sponge.getRegistry().getType(Attribute.class, key);
            if (type1.isPresent()) {
                Attribute attribute = type1.get();
                int anInt = entry.getValue().getInt();
                map.put(attribute, anInt);
            } else {
                warn("Unknown attribute " + key + ". Should be one of: " +
                        Sponge.getRegistry().getAllOf(Attribute.class)
                                .stream()
                                .map(Attribute::getId)
                                .collect(Collectors.joining(", "))
                );
            }
        }
        return map;
    }

    @Override
    public void serialize(TypeToken<?> type, @Nullable Map<Attribute, Integer> obj, ConfigurationNode value) throws ObjectMappingException {

    }
}
