package cz.neumimto.rpg.entities;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.Living;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by NeumimTo on 20.12.2015.
 */
@Singleton
public class MobSettingsDao {

	public Map<EntityType, Double> getDamages() {
		File properties = createDefaults("MobDamage.properties");
		Properties prop = load(properties);
		return getMap(prop);
	}

	private Map<EntityType, Double> getMap(Properties prop) {
		Stream<Map.Entry<Object, Object>> stream = prop.entrySet().stream();
		return stream.collect(Collectors.toMap(
				e -> Sponge.getGame().getRegistry().getType(EntityType.class, (String) e.getKey()).get(),
				e -> Double.parseDouble((String) e.getValue())));
	}


	public Map<EntityType, Double> getHealth() {
		File properties = createDefaults("MobHealth.properties");
		Properties prop = load(properties);
		return getMap(prop);
	}

	public Map<EntityType, Double> getExperiences() {
		File properties = createDefaults("MobExperiences.properties");
		Properties prop = load(properties);
		return getMap(prop);
	}

	private Properties load(File properties) {
		Properties prop = new Properties();
		try (FileInputStream stream = new FileInputStream(properties)) {
			prop.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

	private File createDefaults(String s) {
		File properties = new File(NtRpgPlugin.workingDir, s);
		if (!properties.exists()) {
			Collection<EntityType> types = Sponge.getGame().getRegistry().getAllOf(EntityType.class);
			try {
				properties.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try (FileOutputStream stream = new FileOutputStream(properties)) {
				types.stream().filter(e -> Living.class.isAssignableFrom(e.getEntityClass())).filter(e -> !Human.class.isAssignableFrom(e.getEntityClass())).forEach(a -> {
					try {

						stream.write((a.getName() + " : 10" + Utils.LineSeparator).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				stream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}
}
