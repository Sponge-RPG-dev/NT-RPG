package cz.neumimto.rpg.exp;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by NeumimTo on 8.4.2017.
 */
@Singleton
public class ExperienceDAO {

	public Map<String, Double> getExperiencesForMinerals() {
		File defaults = createDefaults("Minning_experiences.properties");
		return getStringDoubleMap(defaults);
	}

	public Map<String, Double> getExperiencesForWoodenBlocks() {
		File defaults = createDefaults("Logging_experiences.properties");
		return getStringDoubleMap(defaults);
	}

	private Map<String, Double> getStringDoubleMap(File defaults) {
		Map<String, Double> map = new HashMap<>();
		try (FileInputStream stream =  new FileInputStream(defaults)){
			Properties properties = new Properties();
			properties.load(stream);
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				map.put(entry.getKey().toString(), Double.parseDouble(entry.getValue().toString()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private File createDefaults(String s) {
		File properties = new File(NtRpgPlugin.workingDir, s);
		if (!properties.exists()) {
			try {
				properties.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}
}
