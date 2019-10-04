package cz.neumimto.rpg.api.configuration;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 24.2.2019.
 */
public class ClassTypesDeserializer implements Converter<Map<String, ClassTypeDefinition>, Config> {

    @Override
    public Map<String, ClassTypeDefinition> convertToField(Config value) {
  /*      Map<String, ClassTypeDefinition> classTypeDefinitionMap = new LinkedHashMap<>();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : childrenMap.entrySet()) {
            String key = (String) entry.getKey();
            ClassTypeDefinition value = entry.getValue().getValue(TypeToken.of(ClassTypeDefinition.class));
            classTypeDefinitionMap.put(key, value);
        }
*/
        return new HashMap<>();
    }

    @Override
    public Config convertFromField(Map<String, ClassTypeDefinition> value) {
        return null;
    }
}
