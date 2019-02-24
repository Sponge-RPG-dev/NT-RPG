package cz.neumimto.rpg.configuration;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by NeumimTo on 24.2.2019.
 */
public class ClassTypesDeserializer implements TypeSerializer<Map<String, ClassTypeDefinition>> {

    @Override
    public Map<String, ClassTypeDefinition> deserialize(TypeToken<?> typeToken, ConfigurationNode node)
            throws ObjectMappingException {
        Map<Object, ? extends ConfigurationNode> childrenMap = node.getChildrenMap();
        TreeMap<String, ClassTypeDefinition> treeMap = new TreeMap();

        return treeMap;
    }

    @Override
    public void serialize(TypeToken<?> typeToken, Map<String, ClassTypeDefinition> stringClassTypeDefinitionMap, ConfigurationNode configurationNode)
            throws ObjectMappingException {
        configurationNode.setValue(stringClassTypeDefinitionMap);
    }
}
