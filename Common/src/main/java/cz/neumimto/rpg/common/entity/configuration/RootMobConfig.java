package cz.neumimto.rpg.common.entity.configuration;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 7.5.2018.
 */
public class RootMobConfig {

    @Path("dimmensions")
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
