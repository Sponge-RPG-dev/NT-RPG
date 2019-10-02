package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Converter;
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
public class AttributeMapAdapter implements Converter<Map<AttributeConfig, Integer>, Map<String, Integer>> {

    @Override
    public Map<AttributeConfig, Integer> convertToField(Map<String, Integer> value) {
        Map<AttributeConfig, Integer> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : value.entrySet()) {
            String key = entry.getKey();

            Optional<AttributeConfig> type1 = Rpg.get().getPropertyService().getAttributeById(key);
            if (type1.isPresent()) {
                AttributeConfig attribute = type1.get();
                int anInt = entry.getValue();
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
    public Map<String, Integer> convertFromField(Map<AttributeConfig, Integer> obj) {
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<AttributeConfig, Integer> entry : obj.entrySet()) {
            map.put(entry.getKey().getId(), entry.getValue());
        }

        return map;
    }
}
