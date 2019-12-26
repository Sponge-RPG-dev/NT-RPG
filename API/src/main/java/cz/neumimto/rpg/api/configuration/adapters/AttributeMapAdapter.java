package cz.neumimto.rpg.api.configuration.adapters;

import static cz.neumimto.rpg.api.logging.Log.warn;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 11.3.2019.
 */
public class AttributeMapAdapter implements Converter<Map<AttributeConfig, Integer>, Config> {

    @Override
    public Map<AttributeConfig, Integer> convertToField(Config value) {
        Map<AttributeConfig, Integer> map = new HashMap<>();
        if (value != null) {
            for (Map.Entry<String, Object> entry : value.valueMap().entrySet()) {
                String key = entry.getKey();

                Optional<AttributeConfig> attribute = Rpg.get().getPropertyService().getAttributeById(key);
                if (attribute.isPresent()) {
                    int anInt = ((Number) entry.getValue()).intValue();
                    map.put(attribute.get(), anInt);
                } else {
                    warn("Unknown attribute " + key + ". Should be one of: " +
                            String.join(", ", Rpg.get().getPropertyService().getAttributes().keySet())
                    );
                }
            }
        }
        return map;
    }

    @Override
    public Config convertFromField(Map<AttributeConfig, Integer> obj) {
        Config config = Config.inMemory();
        for (Map.Entry<AttributeConfig, Integer> entry : obj.entrySet()) {
            config.add(entry.getKey().getId(), entry.getValue());
        }
        return config;
    }
}
