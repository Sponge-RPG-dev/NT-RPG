package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class PropertiesMapAdapter implements Converter<Map<Integer, Float>, Map<String, Float>> {


    @Override
    public Map<Integer, Float> convertToField(Map<String, Float> value) {
        Map<Integer, Float> map = new HashMap<>();

        IPropertyService propertyService = Rpg.get().getPropertyService();
        for (Map.Entry<String, Float> objectEntry : value.entrySet()) {
            String propertyName = (objectEntry.getKey()).toLowerCase();
            float f = objectEntry.getValue();
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
    public Map<String, Float> convertFromField(Map<Integer, Float> value) {

        Map<String, Float> floatMap = new HashMap<>();
        IPropertyService propertyService = Rpg.get().getPropertyService();

        for (Map.Entry<Integer, Float> integerFloatEntry : value.entrySet()) {
            Integer key = integerFloatEntry.getKey();
            String nameById = propertyService.getNameById(key);
            if (nameById == null) {
                continue;
            }
            floatMap.put(nameById, integerFloatEntry.getValue());
        }
        return floatMap;
    }
}
