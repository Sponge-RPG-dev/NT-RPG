package cz.neumimto.rpg.entities;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by NeumimTo on 20.12.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class MobSettingsDao {

	private RootMobConfig cache;

	@Listener
	public void load(GameStartedServerEvent event) {
		cache = createDefaults("MobSettings.conf");
	}

	private RootMobConfig createDefaults(String s) {
		File properties = new File(NtRpgPlugin.workingDir, s);
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
				overWorldMobConfig.getDamage().put(livingEntity, 10D);
				overWorldMobConfig.getExperiences().put(livingEntity, 10D);
				overWorldMobConfig.getHealth().put(livingEntity, 10D);
			}
			Collection<WorldProperties> allWorldProperties = Sponge.getServer().getAllWorldProperties();
			for (WorldProperties allWorldProperty : allWorldProperties) {
				rootMobConfig.getDimmensions().put(allWorldProperty.getWorldName(), overWorldMobConfig);
			}


			try {
				ObjectMapper.BoundInstance configMapper = ObjectMapper.forObject(rootMobConfig);
				HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(properties.toPath()).build();
				SimpleConfigurationNode scn = SimpleConfigurationNode.root();
				configMapper.serialize(scn);
				hcl.save(scn);
			} catch (Exception e) {
				throw new RuntimeException("Could not create file " + s);
			}
		}
		try {
			ObjectMapper<RootMobConfig> mapper = ObjectMapper.forClass(RootMobConfig.class);
			HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(properties.toPath()).build();
			return mapper.bind(new RootMobConfig()).populate(hcl.load());
		} catch (Exception e) {
			throw new RuntimeException("Could not load file " + s);
		}
	}

	public RootMobConfig getCache() {
		return cache;
	}
}
