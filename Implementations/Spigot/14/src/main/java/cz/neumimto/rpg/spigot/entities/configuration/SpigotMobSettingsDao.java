package cz.neumimto.rpg.spigot.entities.configuration;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.configuration.MobsConfig;
import cz.neumimto.rpg.common.entity.configuration.RootMobConfig;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

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
                    .filter(e -> !LivingEntity.class.isAssignableFrom(e.getEntityClass()))
                    .filter(e -> HumanEntity.class.isAssignableFrom(e.getEntityClass()))
                    .collect(Collectors.toList());

            RootMobConfig rootMobConfig = new RootMobConfig();
            MobsConfig overWorldMobConfig = new MobsConfig();

            for (EntityType livingEntity : livingEntities) {
                overWorldMobConfig.getDamage().put(livingEntity.getName(), 10D);
                overWorldMobConfig.getExperiences().put(livingEntity.getName(), 10D);
                overWorldMobConfig.getHealth().put(livingEntity.getName(), 10D);
            }

            List<World> worlds = Bukkit.getWorlds();
            for (World w : worlds) {
                rootMobConfig.getDimmensions().put(w.getName(), overWorldMobConfig);
            }
            FileUtils.generateConfigFile(rootMobConfig, properties);


        }
        try {
            ObjectMapper<RootMobConfig> mapper = ObjectMapper.forClass(RootMobConfig.class);
            HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(properties.toPath()).build();
            return mapper.bind(new RootMobConfig()).populate(hcl.load());
        } catch (Exception e) {
            throw new RuntimeException("Could not load file MobSettings.conf", e);
        }
    }
}
