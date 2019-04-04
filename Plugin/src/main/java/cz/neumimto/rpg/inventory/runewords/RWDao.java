package cz.neumimto.rpg.inventory.runewords;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import cz.neumimto.rpg.common.logging.Log;

import javax.inject.Singleton;
import java.io.File;
import java.util.*;

import static cz.neumimto.rpg.common.logging.Log.info;
import static cz.neumimto.rpg.common.logging.Log.warn;

/**
 * Created by NeumimTo on 29.10.2015.
 */
@Singleton
public class RWDao {

	public Set<Rune> getAllRunes(File p) {
		Set<Rune> s = new HashSet<>();
		Config config = ConfigFactory.parseFile(p);
		info("Loading runes from " + p.getName());
		List<String> runes = config.getStringList("Runes");
		for (String a : runes) {
			Rune r = new Rune();
			r.setName(a);
			s.add(r);
		}
		return s;
	}

	public Set<RuneWordTemplate> getAllRws(File p) {
		Set<RuneWordTemplate> s = new HashSet<>();
		Config configr = ConfigFactory.parseFile(p);
		info("Loading runewords from " + p.getName());
		final String root = "RuneWords";


		List<? extends ConfigObject> objectList = configr.getObjectList(root);
		for (ConfigObject configObject : objectList) {
			Config config = configObject.toConfig();
			RuneWordTemplate rw = new RuneWordTemplate();
			try {
				String name = config.getString("Name");
				rw.setName(name);
			} catch (RuntimeException e) {
				Log.error("Runeword at index: " + s.size() + 1 + " wont be loaded, missing Name node");
				continue;
			}

			int minlevel = 0;
			try {
				minlevel = config.getInt("MinLevel");
			} catch (RuntimeException ignored) {
			}

			List<String> allowed;
			try {
				allowed = config.getStringList("AllowedGroups");
			} catch (RuntimeException e) {
				allowed = new ArrayList<>();
			}
			List<String> allowedItems;

			try {
				allowedItems = config.getStringList("AllowedItems");
			} catch (RuntimeException e) {
				allowedItems = new ArrayList<>();
			}


			rw.setAllowedItems(allowedItems);

			try {
				List<String> eff = config.getStringList("Effects");
				Map<String, String> map = new HashMap<>();
				for (String s1 : eff) {
					String[] split = s1.split(":");
					String k = split[0];
					String v = "";
					if (split.length > 1) {
						v = split[1];
					}
					map.put(k, v);
				}
				rw.setEffects(map);
			} catch (RuntimeException ignored) {

			}


			List<String> runes;
			try {
				runes = config.getStringList("Runes");
			} catch (RuntimeException e) {
				runes = new ArrayList<>();
				warn("Runeword " + rw.getName() + " has no rune combination defined");
			}


			rw.setMinLevel(minlevel);
			rw.setAllowedGroups(allowed);
			rw.setRunes(runes);

			s.add(rw);
		}

		return s;
	}


}
