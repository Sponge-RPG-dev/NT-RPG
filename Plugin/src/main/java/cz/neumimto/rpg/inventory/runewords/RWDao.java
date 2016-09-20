package cz.neumimto.rpg.inventory.runewords;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import cz.neumimto.core.ioc.Singleton;

import java.io.File;
import java.util.*;

/**
 * Created by NeumimTo on 29.10.2015.
 */
@Singleton
public class RWDao {

    public Set<Rune> getAllRunes(File p) {
        Set<Rune> s = new HashSet<>();
        Config config = ConfigFactory.parseFile(p);
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
        final String root = "RuneWords";
        ConfigObject rws = config.getObject(root);
        Config c = rws.toConfig();
        for (String a : rws.keySet()) {
            RuneWordTemplate rw = new RuneWordTemplate();
            String name = config.getString(root + "." + a + ".Name");
            int minlevel = config.getInt(root + "." + a + ".MinLevel");
            List<String> restricted = config.getStringList(root + "." + a + ".RestrictedClasses");
            List<String> allowedItems = config.getStringList(root + "." + a + ".AllowedItems");
            rw.setAllowedItems(allowedItems);
            ConfigObject object = config.getObject(root + "." + a + ".Effects");
            Map<String, Float> map = new HashMap<>();
            for (String s1 : object.keySet()) {
                ConfigValue configValue = object.get(s1);
                float v = Float.parseFloat(configValue.render());
                map.put(s1,v);
            }
            List<String> runes = config.getStringList(root + "." + a + ".Runes");
            rw.setName(name);
            rw.setMinLevel(minlevel);
            rw.setRestrictedClasses(restricted);
            rw.setRunes(runes);
            rw.setEffects(map);
            s.add(rw);
        }
        return s;
    }


}
