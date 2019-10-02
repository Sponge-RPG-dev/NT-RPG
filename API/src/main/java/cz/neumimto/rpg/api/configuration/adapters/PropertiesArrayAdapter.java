package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 5.1.2019.
 */
public class PropertiesArrayAdapter implements Converter<float[], Map<String, Float>> {

    @Override
    public float[] convertToField(Map<String, Float> value) {
        int lastId = Rpg.get().getPropertyService().getLastId();
        float[] arr = new float[lastId];

        IPropertyService propertyService = Rpg.get().getPropertyService();
        for (Map.Entry<String, Float> objectEntry : value.entrySet()) {
            String propertyName = ((String) objectEntry.getKey()).toLowerCase();
            float f = ((Number) objectEntry.getValue()).floatValue();

            if (propertyService.exists(propertyName)) {
                int idByName = propertyService.getIdByName(propertyName);
                arr[idByName] = f;
            } else {
                Log.error("Unknown property " + propertyName);
            }
        }

        return arr;
    }

    @Override
    public Map<String, Float> convertFromField(float[] floats) {
        Map<String, Float> map = new HashMap<>();
        IPropertyService propertyService = Rpg.get().getPropertyService();
        for (int i = 0; i < floats.length; i++) {
            String nameById = propertyService.getNameById(i);
            if (floats[i] != 0) {
                map.put(nameById, floats[i]);
            }
        }
    }
}
