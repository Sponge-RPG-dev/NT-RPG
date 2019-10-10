package cz.neumimto.rpg.sponge.entities.configuration;

import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.configuration.MobsConfig;
import cz.neumimto.rpg.common.entity.configuration.RootMobConfig;
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

@Singleton
@IResourceLoader.ListenerClass
public class SpongeMobSettingsDao extends MobSettingsDao {

    @Override
    protected RootMobConfig createDefaults()  {
        File properties = new File(Rpg.get().getWorkingDirectory(), "MobSettings.conf");
        if (!properties.exists()) {
            Collection<EntityType> types = Sponge.getGame().getRegistry().getAllOf(EntityType.class);
            List<EntityType> livingEntities = new ArrayList<>();

            types.stream()
                    .filter(e -> Living.class.isAssignableFrom(e.getEntityClass()))
                    .filter(e -> !Human.class.isAssignableFrom(e.getEntityClass()))
                    .forEach(livingEntities::add);

            RootMobConfig rootMobConfig = new RootMobConfig();
            MobsConfig overWorldMobConfig = new MobsConfig();
            for (EntityType livingEntity : livingEntities) {
                overWorldMobConfig.getDamage().put(livingEntity.getId(), 10D);
                overWorldMobConfig.getExperiences().put(livingEntity.getId(), 10D);
                overWorldMobConfig.getHealth().put(livingEntity.getId(), 10D);
            }
            Collection<WorldProperties> allWorldProperties = Sponge.getServer().getAllWorldProperties();
            for (WorldProperties allWorldProperty : allWorldProperties) {
                rootMobConfig.getDimmensions().put(allWorldProperty.getWorldName(), overWorldMobConfig);
            }
            FileUtils.generateConfigFile(rootMobConfig, properties);

        }
        return loadFile(properties.toPath());
    }


    @Listener
    public void load(GameStartedServerEvent event) {
        load();
    }

}
