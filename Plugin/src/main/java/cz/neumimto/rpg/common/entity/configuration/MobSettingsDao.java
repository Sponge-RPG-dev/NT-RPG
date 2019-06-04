package cz.neumimto.rpg.common.entity.configuration;

import cz.neumimto.rpg.common.entity.configuration.MobsConfig;
import cz.neumimto.rpg.common.entity.configuration.RootMobConfig;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.common.utils.io.FileUtils;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.world.storage.WorldProperties;

import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
