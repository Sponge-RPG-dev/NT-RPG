package cz.neumimto.rpg.common.entity.configuration;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 7.5.2018.
 */
public class MobsConfig {

    @Path("damage")
    @Conversion(SDMap.class)
    private Map<String, Double> damage;

    @Path("experiences")
    @Conversion(SDMap.class)
    private Map<String, Double> experiences;

    @Path("health")
    @Conversion(SDMap.class)
    private Map<String, Double> health;

    public MobsConfig() {
        this.damage = new HashMap<>();
        this.experiences = new HashMap<>();
        this.health = new HashMap<>();
    }

    public Map<String, Double> getDamage() {
        return damage;
    }

    public void setDamage(Map<String, Double> damage) {
        this.damage = damage;
    }

    public Map<String, Double> getExperiences() {
        return experiences;
    }

    public void setExperiences(Map<String, Double> experiences) {
        this.experiences = experiences;
    }

    public Map<String, Double> getHealth() {
        return health;
    }

    public void setHealth(Map<String, Double> health) {
        this.health = health;
    }

    private static class SDMap implements Converter<Map<String, Double>, Config> {
        @Override
        public Map<String, Double> convertToField(Config value) {
            if (value == null) {
                return new HashMap<>();
            }
            Map<String, Double> m = new HashMap<>();
            Map<String, Object> e = value.valueMap();
            for (Map.Entry<String, Object> entry : e.entrySet()) {
                m.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
            }
            return m;
        }

        @Override
        public Config convertFromField(Map<String, Double> value) {
            Config config = Config.inMemory();
            if (value == null) {
                return config;
            }
            for (Map.Entry<String, Double> entry : value.entrySet()) {
                config.set(entry.getKey(), entry.getValue());
            }
            return config;
        }
    }
}
