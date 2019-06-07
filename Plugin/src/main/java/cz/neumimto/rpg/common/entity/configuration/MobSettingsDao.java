package cz.neumimto.rpg.common.entity.configuration;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

/**
 * Created by NeumimTo on 20.12.2015.
 */
public abstract class MobSettingsDao {

    private RootMobConfig cache;

    @Listener
    public void load(GameStartedServerEvent event) {
        cache = createDefaults("MobSettings.conf");
    }

    protected abstract RootMobConfig createDefaults(String s);

    public RootMobConfig getCache() {
        return cache;
    }
}
