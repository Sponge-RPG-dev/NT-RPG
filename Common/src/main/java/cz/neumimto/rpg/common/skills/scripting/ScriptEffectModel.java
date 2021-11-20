package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptEffectModel {

    @Path("Id")
    public String id;

    @Path("Effect-Types")
    @Optional
    public List<String> skillTypes;

    @Optional
    @Path("SuperType")
    public String superType;

    @Optional
    @Path("Fields")
    @Conversion(SSMap.class)
    public Map<String, String> fields;

    @Optional
    @Path("OnApply")
    public String onApply;

    @Optional
    @Path("OnTick")
    public String onTick;

    @Optional
    @Path("OnRemove")
    public String onRemove;

    private static class SSMap implements Converter<Map<String, String>, Config> {
        @Override
        public Map<String, String> convertToField(Config value) {
            if (value == null) {
                return new HashMap<>();
            }
            Map<String, String> m = new HashMap<>();
            Map<String, Object> e = value.valueMap();
            for (Map.Entry<String, Object> entry : e.entrySet()) {
                m.put(entry.getKey(), entry.getValue().toString());
            }
            return m;
        }

        @Override
        public Config convertFromField(Map<String, String> value) {
            Config config = Config.inMemory();
            if (value == null) {
                return config;
            }
            for (Map.Entry<String, String> entry : value.entrySet()) {
                config.set(entry.getKey(), entry.getValue());
            }
            return config;
        }
    }
}
