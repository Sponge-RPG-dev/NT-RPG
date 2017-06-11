package cz.neumimto.rpg.inventory.runewords;

import com.google.inject.ConfigurationException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.MissingConfigurationException;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;

/**
 * Created by NeumimTo on 29.10.2015.
 */
@Singleton
public class RWDao {

    @Inject
    private Logger logger;

    public Set<Rune> getAllRunes(File p) {
        Set<Rune> s = new HashSet<>();
        Config config = ConfigFactory.parseFile(p);
        logger.info("Loading runes from " + p.getName());
        ConfigObject runes = config.getObject("Runes");
        for (String a : runes.keySet()) {
            Rune r = new Rune();
            r.setName(a);
            r.setSpawnchance(0);
            s.add(r);
        }
        return s;
    }

    public Set<RuneWordTemplate> getAllRws(File p) {
        Set<RuneWordTemplate> s = new HashSet<>();
        Config config = ConfigFactory.parseFile(p);
        logger.info("Loading runewords from " + p.getName());
        final String root = "RuneWords";
        ConfigObject rws = config.getObject(root);
        Config c = rws.toConfig();
        for (String a : rws.keySet()) {
            RuneWordTemplate rw = new RuneWordTemplate();
            try {
                String name = config.getString(root + "." + a + ".Name");
                rw.setName(name);
            } catch (RuntimeException e) {
                logger.error("Runeword at index: " + s.size() +1 + " wont be loaded, missing Name node");
                continue;
            }

            int minlevel = 0;
            try {
                minlevel = config.getInt(root + "." + a + ".MinLevel");
            } catch (RuntimeException ignored) {}

            List<String> allowed;
            try {
                allowed = config.getStringList(root + "." + a + ".AllowedGroups");
            } catch (RuntimeException e) {
                allowed = new ArrayList<>();
            }
            List<String> allowedItems;

            try {
                allowedItems= config.getStringList(root + "." + a + ".AllowedItems");
            } catch (RuntimeException e) {
                allowedItems = new ArrayList<>();
            }


            rw.setAllowedItems(allowedItems);

            try {
                List<String> eff = config.getStringList(root + "." + a + ".Effects");
                Map<String, String> map = new HashMap<>();
                for (String s1 : eff) {
                    String[] split = s1.split(":");
                    String k = split[0];
                    String v = null;
                    if (split.length > 1 ) {
                        v = split[1];
                    }
                    map.put(k,v);
                }
                rw.setEffects(map);
            } catch (RuntimeException ignored) {

            }


            List<String> runes;
            try {
                runes = config.getStringList(root + "." + a + ".Runes");
            } catch (RuntimeException e) {
                runes = new ArrayList<>();
                logger.warn("Runeword " + rw.getName() + " has no rune combination defined");
            }


            rw.setMinLevel(minlevel);
            rw.setAllowedGroups(allowed);
            rw.setRunes(runes);

            s.add(rw);
        }
        return s;
    }


}
