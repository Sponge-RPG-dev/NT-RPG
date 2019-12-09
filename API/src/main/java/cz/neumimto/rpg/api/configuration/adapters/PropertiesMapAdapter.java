package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class PropertiesMapAdapter implements Converter<Map<Integer, Float>, Config> {

    @Override
    public Map<Integer, Float> convertToField(Config c) {
        Map<Integer, Float> map = new HashMap<>();
        Map<String, Object> valueMap = c.valueMap();
        PropertyService propertyService = Rpg.get().getPropertyService();
        for (Map.Entry<String, Object> objectEntry : valueMap.entrySet()) {
            String propertyName = (objectEntry.getKey()).toLowerCase();
            float f = ((Number) objectEntry.getValue()).floatValue();
            if (propertyService.exists(propertyName)) {
                int idByName = propertyService.getIdByName(propertyName);
                map.put(idByName, f);
            } else {
                Log.warn("Unknown property " + propertyName);
            }
        }

        return map;
    }

    @Override
    public Config convertFromField(Map<Integer, Float> value) {
        //NOOP
        return Config.inMemory();
    }
}
