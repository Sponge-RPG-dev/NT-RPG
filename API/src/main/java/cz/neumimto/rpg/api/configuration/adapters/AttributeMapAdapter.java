package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.warn;

/**
 * Created by NeumimTo on 11.3.2019.
 */
public class AttributeMapAdapter implements Converter<Map<AttributeConfig, Integer>, Config> {

    @Override
    public Map<AttributeConfig, Integer> convertToField(Config value) {
        Map<AttributeConfig, Integer> map = new HashMap<>();

        Map<String, Object> stringObjectMap = value.valueMap();

        for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
            String key = entry.getKey();

            Optional<AttributeConfig> type1 = Rpg.get().getPropertyService().getAttributeById(key);
            if (type1.isPresent()) {
                AttributeConfig attribute = type1.get();
                int anInt = ((Number)entry.getValue()).intValue();
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
    public Config convertFromField(Map<AttributeConfig, Integer> obj) {
        Config config = new ObjectConverter().toConfig(obj, Config::inMemory);

        return config;
    }
}
