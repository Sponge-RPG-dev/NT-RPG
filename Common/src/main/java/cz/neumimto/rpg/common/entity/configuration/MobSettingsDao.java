package cz.neumimto.rpg.common.entity.configuration;

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
}
