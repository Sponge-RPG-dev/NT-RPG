package cz.neumimto.rpg.sponge.configuration;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 24.2.2019.
 */
public class ClassTypesDeserializer implements TypeSerializer<Map<String, ClassTypeDefinition>> {

    @Override
    public Map<String, ClassTypeDefinition> deserialize(TypeToken<?> typeToken, ConfigurationNode node)
            throws ObjectMappingException {

        Map<Object, ? extends ConfigurationNode> childrenMap = node.getChildrenMap();
        Map<String, ClassTypeDefinition> classTypeDefinitionMap = new LinkedHashMap<>();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : childrenMap.entrySet()) {
            String key = (String) entry.getKey();
            ClassTypeDefinition value = entry.getValue().getValue(TypeToken.of(ClassTypeDefinition.class));
            classTypeDefinitionMap.put(key, value);
        }

        return classTypeDefinitionMap;
    }

    @Override
    public void serialize(TypeToken<?> typeToken, Map<String, ClassTypeDefinition> stringClassTypeDefinitionMap, ConfigurationNode configurationNode)
            throws ObjectMappingException {
        configurationNode.setValue(stringClassTypeDefinitionMap);
    }
}
