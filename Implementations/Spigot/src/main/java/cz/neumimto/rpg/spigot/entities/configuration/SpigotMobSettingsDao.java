package cz.neumimto.rpg.spigot.entities.configuration;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.configuration.MobsConfig;
import cz.neumimto.rpg.common.entity.configuration.RootMobConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import javax.inject.Singleton;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class SpigotMobSettingsDao extends MobSettingsDao {

    @Override
    protected RootMobConfig createDefaults() {
        File properties = new File(Rpg.get().getWorkingDirectory(), "MobSettings.conf");

        if (!properties.exists()) {
            EntityType[] values = EntityType.values();
            List<EntityType> livingEntities = Stream.of(values)
                    .filter(EntityType::isAlive)
                    .collect(Collectors.toList());

            RootMobConfig rootMobConfig = new RootMobConfig();
            MobsConfig overWorldMobConfig = new MobsConfig();

            for (EntityType livingEntity : livingEntities) {
                overWorldMobConfig.getDamage().put(livingEntity.name(), 10D);
                overWorldMobConfig.getExperiences().put(livingEntity.name(), 10D);
                overWorldMobConfig.getHealth().put(livingEntity.name(), 10D);
            }

            List<World> worlds = Bukkit.getWorlds();
            for (World w : worlds) {
                rootMobConfig.getDimmensions().put(w.getName(), overWorldMobConfig);
            }
            FileUtils.generateConfigFile(rootMobConfig, properties);


        }
        return loadFile(properties.toPath());
    }
}
