package cz.neumimto.rpg.common.configuration.adapters;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringHashSetAdapter implements TypeSerializer<Set<String>> {
    @Override
    public Set<String> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
        List<String> list = configurationNode.getList(TypeToken.of(String.class));
        return new HashSet<>(list);
    }

    @Override
    public void serialize(TypeToken<?> typeToken, Set<String> stringSet, ConfigurationNode configurationNode) throws ObjectMappingException {
        configurationNode.setValue(stringSet);
    }
}
