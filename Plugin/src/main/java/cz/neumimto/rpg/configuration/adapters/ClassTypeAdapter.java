package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public class ClassTypeAdapter implements TypeSerializer<String> {


    @Override
    public String deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException{
        String string = configurationNode.getString();
        for (String class_type : NtRpgPlugin.pluginConfig.CLASS_TYPES.keySet()) {
            if (string.equalsIgnoreCase(class_type)) {
                return class_type;
            }
        }
        String all = String.join(", ", NtRpgPlugin.pluginConfig.CLASS_TYPES.keySet());
        throw new ObjectMappingException("Unknown class type \""+string+"\", must be one of " + all);
    }

    @Override
    public void serialize(TypeToken<?> typeToken, String s, ConfigurationNode configurationNode) {
        configurationNode.setValue(s);
    }
}
