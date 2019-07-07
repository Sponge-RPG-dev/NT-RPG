package cz.neumimto.rpg.common.entity.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 7.5.2018.
 */
@ConfigSerializable
public class RootMobConfig {

    @Setting(value = "dimensions", comment = "Dimensions")
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
}
