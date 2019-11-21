package cz.neumimto.rpg.api.configuration;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 24.2.2019.
 */
public class ClassTypesDeserializer implements Converter<Map<String, ClassTypeDefinition>, Config> {

    @Override
    public Map<String, ClassTypeDefinition> convertToField(Config value) {
        Map<String, ClassTypeDefinition> map = new LinkedHashMap<>();

        Map<String, Object> m = value.valueMap();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            CommentedConfig v = (CommentedConfig) entry.getValue();
            String key = entry.getKey();
            map.put(key, new ObjectConverter().toObject(v, ClassTypeDefinition::new));
        }

        return new HashMap<>();
    }

    @Override
    public Config convertFromField(Map<String, ClassTypeDefinition> value) {
        Config config = Config.inMemory();
        for (Map.Entry<String, ClassTypeDefinition> entry : value.entrySet()) {
            config.set(entry.getKey(), new ObjectConverter().toConfig(entry.getValue(), Config::inMemory));
        }
        return config;
    }
}
