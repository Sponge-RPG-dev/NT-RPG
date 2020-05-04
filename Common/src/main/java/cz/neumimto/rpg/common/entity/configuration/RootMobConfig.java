package cz.neumimto.rpg.common.entity.configuration;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 7.5.2018.
 */
public class RootMobConfig {

    @Path("dimensions")
    @Conversion(RMCConversion.class)
    private Map<String, MobsConfig> dimmensions;

    public RootMobConfig() {
        dimmensions = new HashMap<>();
    }

    public Map<String, MobsConfig> getDimmensions() {
        return dimmensions;
    }

    public void setDimmensions(Map<String, MobsConfig> dimmensions) {
        this.dimmensions = dimmensions;
    }

    public MobsConfig getDimmension(String worldName) {
        return dimmensions.get(worldName);
    }

    private static class RMCConversion implements Converter<Map<String, MobsConfig>, Config> {

        @Override
        public Map<String, MobsConfig> convertToField(Config value) {
            if (value == null) {
                return new HashMap<>();
            }
            Map<String, MobsConfig> dimMap = new HashMap<>();
            Map<String, Object> stringObjectMap = value.valueMap();
            for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
                Config c = (Config) entry.getValue();
                String key = entry.getKey();
                dimMap.put(key, new ObjectConverter().toObject(c, MobsConfig::new));
            }

            return dimMap;
        }

        @Override
        public Config convertFromField(Map<String, MobsConfig> value) {
            Config config = Config.inMemory();
            if (value != null) {
                Config sub = Config.inMemory();
                for (Map.Entry<String, MobsConfig> entry : value.entrySet()) {
                    new ObjectConverter().toConfig(entry.getValue(), sub);
                    config.add(entry.getKey(), sub);
                    sub = Config.inMemory();
                }
            }
            return config;
        }
    }
}
