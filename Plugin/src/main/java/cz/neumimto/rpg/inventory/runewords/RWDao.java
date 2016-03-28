package cz.neumimto.rpg.inventory.runewords;

import com.typesafe.config.*;
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
            String name = config.getString(root+"."+a+".Name");
            int minlevel = config.getInt(root + "." + a + ".MinLevel");
            List<String> restricted = config.getStringList(root + "." + a + ".RestrictedClasses");
            List<String> allowedItems = config.getStringList(root + "." + a + ".AllowedItems");
            rw.setAllowedItems(allowedItems);
            List<String> effects = config.getStringList(root+"."+a+".Effects");
            Map<String,Float> map = new HashMap<>();
            effects.stream().map(q -> q.split(":")).forEach(d -> map.put(d[0],Float.parseFloat(d[1])));
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
