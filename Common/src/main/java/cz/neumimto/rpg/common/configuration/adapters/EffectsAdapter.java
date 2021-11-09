package cz.neumimto.rpg.common.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.Path;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectParams;
import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.logging.Log;

import java.util.*;

public class EffectsAdapter implements Converter<Map<IGlobalEffect, EffectParams>, List<Config>> {

    @Override
    public Map<IGlobalEffect, EffectParams> convertToField(List<Config> value) {
        Map<IGlobalEffect, EffectParams> params = new HashMap<>();

        if (value != null) {
            for (Config config : value) {

                EffectConfigModel model = new ObjectConverter().toObject(config, EffectConfigModel::new);

                if (model.type == null) {
                    Log.warn("Cannot read effects section - Missing node Id");
                    continue;
                }
                IGlobalEffect globalEffect = Rpg.get().getEffectService().getGlobalEffect(model.type);
                if (globalEffect == null) {
                    Log.error("Unknown Effect " + model.type);
                    continue;
                }
                if (model.settings == null) {
                    model.settings = new HashMap<>();
                }
                params.put(globalEffect, new EffectParams(model.settings));

            }
        }
        return params;
    }

    @Override
    public List<Config> convertFromField(Map<IGlobalEffect, EffectParams> value) {
        //NOOP
        return new ArrayList<>();
    }

    protected static class EffectConfigModel {

        @Path("Id")
        private String type;

        @Path("Settings")
        @Conversion(EffectSettingsConverter.class)
        private Map<String, String> settings = new HashMap<>();

    }

    private static class EffectSettingsConverter implements Converter<Map<String, String>, Config> {

        @Override
        public Map<String, String> convertToField(Config value) {
            if (value == null) {
                return Collections.emptyMap();
            }
            Map<String, String> map = new HashMap<>();
            Map<String, Object> stringObjectMap = value.valueMap();
            for (Map.Entry<String, Object> a : stringObjectMap.entrySet()) {
                map.put(a.getKey(), String.valueOf(a.getValue()));
            }
            return map;
        }

        @Override
        public Config convertFromField(Map<String, String> value) {
            return Config.inMemory();
        }
    }
}
