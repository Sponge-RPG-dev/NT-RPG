package cz.neumimto.rpg.common.entity.configuration;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.nio.file.Path;

/**
 * Created by NeumimTo on 20.12.2015.
 */
public abstract class MobSettingsDao {

    protected RootMobConfig cache;

    protected abstract RootMobConfig createDefaults();

    public RootMobConfig getCache() {
        return cache;
    }

    public void load() {
        cache = createDefaults();
    }

    protected RootMobConfig loadFile(Path path) {
        try (FileConfig fc = FileConfig.of(path)) {
            fc.load();
            return new ObjectConverter().toObject(fc, RootMobConfig::new);
        }
    }
}
