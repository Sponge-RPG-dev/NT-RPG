package cz.neumimto.effects.runewords;

import com.typesafe.config.*;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.Pair;
import cz.neumimto.core.ioc.Singleton;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by NeumimTo on 29.10.2015.
 */
@Singleton
public class RWDao {

    public Set<Rune> getAllRunes() {
        File p = new File(NtRpgPlugin.workingDir,"runes.conf");
        if (!p.exists()) {
            try {
                p.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Set<Rune> s = new HashSet<>();
        Config config = ConfigFactory.parseFile(p);
        ConfigObject runes = config.getObject("Runes");
        for (String a : runes.keySet()) {
            Rune r = new Rune();
            r.setName(a);
            ConfigObject o = config.getObject("Runes." + r);
            Config sub = o.toConfig();
            Set<String> strings = o.keySet();
            List<Pair<String,Double>> l = new ArrayList<>();
            for (String qq : strings) {
                if (qq.equalsIgnoreCase("spawnchance")) {
                    double spawnchance = sub.getDouble("spawnchance");
                    r.setSpawnchance(spawnchance);
                }
                //// TODO
            }

        }
        return s;
    }

    public Set<RuneWord> getAllRws() {
        return Collections.emptySet();
    }
}
