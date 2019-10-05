package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;

import java.util.HashMap;
import java.util.Map;

public class MapStringDoubleAdapter implements Converter<Map<String, Float>, Config> {
    @Override
    public Map convertToField(Config value) {
        Map<String, Float> map = new HashMap();
        for (Map.Entry<String, Object> entry : value.valueMap().entrySet()) {
            Number value1 = (Number) entry.getValue();
            map.put(entry.getKey(), value1.floatValue());
        }
        return map;
    }

    @Override
    public Config convertFromField(Map<String, Float> value) {
        Config config = Config.inMemory();
        new ObjectConverter().toConfig(value, config);
        return config;
    }
}
