/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.players.properties;

import static cz.neumimto.rpg.Log.info;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.utils.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by NeumimTo on 28.12.2014.
 */

@Singleton
public class PropertyService {

	public static final double WALKING_SPEED = 0.1d;
	public static int LAST_ID = 0;
	public static final Supplier<Integer> getAndIncrement = () -> {
		int t = new Integer(LAST_ID);
		LAST_ID++;
		return t;
	};

	private Map<String, Integer> idMap = new HashMap<>();
	private Map<Integer, String> nameMap = new HashMap<>();

	private Map<Integer, Float> defaults = new HashMap<>();
	private Map<String, ICharacterAttribute> attributes = new HashMap<>();

	private float[] maxValues;

	public void registerProperty(String name, int id) {
		info("Found property " + name + "; assigned id: " + id, PluginConfig.DEBUG);
		idMap.put(name, id);
		nameMap.put(id, name);
	}

	public int getIdByName(String name) {
		return idMap.get(name);
	}

	public String getNameById(Integer id) {
		return nameMap.get(id);
	}

	public void registerDefaultValue(int id, float def) {
		defaults.put(id, def);
	}

	public float getDefaultValue(int id) {
		return defaults.get(id);
	}

	public Map<Integer, Float> getDefaults() {
		return defaults;
	}

	public void registerAttribute(ICharacterAttribute attribute) {
		attributes.put(attribute.getName().toLowerCase(), attribute);
	}

	public ICharacterAttribute getAttribute(String name) {
		return attributes.get(name.toLowerCase());
	}

	public Map<String, ICharacterAttribute> getAttributes() {
		return attributes;
	}

	public void init() {
		Path path = Paths.get(NtRpgPlugin.workingDir + File.separator + "properties_dump.info");
		StringBuilder s = new StringBuilder();
		List<String> l = new ArrayList<>(idMap.keySet());
		info(" - found " + l.size() + " Properties", PluginConfig.DEBUG);
		l.sort(Collator.getInstance());
		for (String s1 : l) {
			s.append(s1).append(Utils.LineSeparator);
		}
		try {
			Files.write(path, s.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadMaximalServerPropertyValues() {
		maxValues = new float[LAST_ID];
		for (int i = 0; i < maxValues.length; i++) {
			maxValues[i] = Float.MAX_VALUE;
		}


		Path path = Paths.get(NtRpgPlugin.workingDir, "max_server_property_values.properties");
		File file = path.toFile();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Set<String> missing = new HashSet<>();
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			Properties properties = new Properties();
			properties.load(fileInputStream);
			for (String s : idMap.keySet()) {
				Object o = properties.get(s);
				if (o == null) {
					missing.add(s);
				} else {
					maxValues[getIdByName(s)] = Float.parseFloat(o.toString());
				}
			}

			if (!missing.isEmpty()) {
				missing.forEach(a -> properties.put(a, "10000"));
				FileOutputStream fileOutputStream = FileUtils.openOutputStream(file, false);
				properties.store(fileOutputStream, null);
				fileOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}


	public void setupDefaultProperties(IActiveCharacter character) {
		if (character.isStub())
			return;
		float[] arr = character.getCharacterProperties();
		Map<Integer, Float> defaults = getDefaults();
		for (int i = 0; i < arr.length; i++) {
			if (defaults.containsKey(i)) {
				arr[i] = defaults.get(i);
			} else {
				arr[i] = 0;
			}
		}
	}

	public void process(Class<?> container) {
		int value;
		for (Field f : container.getDeclaredFields()) {
			if (f.isAnnotationPresent(Property.class)) {
				Property p = f.getAnnotation(Property.class);
				value = PropertyService.getAndIncrement.get();
				try {
					f.setInt(null, value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
				if (!p.name().trim().equalsIgnoreCase("")) {
					registerProperty(p.name(), value);
				}
				if (p.default_() != 0f) {
					registerDefaultValue(value, p.default_());
				}
			}
		}
	}

	public float getDefault(Integer key) {
		Float f = defaults.get(key);
		if (f == null)
			return 0;
		return f;
	}

	public float getMaxPropertyValue(int index) {
		return maxValues[index];
	}



}
