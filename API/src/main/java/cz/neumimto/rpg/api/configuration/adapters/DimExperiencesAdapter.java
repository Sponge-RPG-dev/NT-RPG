package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;

import java.util.HashMap;
import java.util.Map;

public class DimExperiencesAdapter implements Converter<Map<String, Map<String, Double>>, Config> {

    @Override
    public Map<String, Map<String, Double>> convertToField(Config value) {
        Map<String, Map<String, Double>> map = new HashMap<>();
        if (value == null) {
            return map;
        }
        Map<String, Object> stringObjectMap = value.valueMap();
        for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
            Map<String, Double> expMap = new HashMap<>();
            Config c = (Config) entry.getValue();
            for (Config.Entry e : c.entrySet()) {
                expMap.put(e.getKey(), e.getValue());
            }
            map.put(entry.getKey(), expMap);
        }
        return map;
    }

    @Override
    public Config convertFromField(Map<String, Map<String, Double>> value) {
        Config config = Config.inMemory();
        return config;
    }
}
