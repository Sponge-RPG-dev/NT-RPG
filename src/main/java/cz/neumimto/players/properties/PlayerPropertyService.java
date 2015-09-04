package cz.neumimto.players.properties;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.Collator;
import java.util.*;

/**
 * Created by NeumimTo on 28.12.2014.
 */

@Singleton
public class PlayerPropertyService {

    public static short LAST_ID = 0;
    private Map<String, Short> idMap = new HashMap<>();
    private Map<Integer, Float> defaults = new HashMap<>();
    private List<PropertyContainer> containerList = new ArrayList<>();
    private Map<String, Short> persistant = new HashMap<>();

    public PlayerPropertyService() {

    }

    public void registerProperty(String name, short id) {
        idMap.put(name, id);
    }

    public Map<String, Short> getPersistantProperties() {
        return persistant;
    }

    public int getIdByName(String name) {
        return idMap.get(name);
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

    public List<PropertyContainer> getContainerList() {
        return containerList;
    }

    @PostProcess(priority = 200)
    public void dump() {
        Path path = Paths.get(NtRpgPlugin.workingDir + File.separator + "properties_dump.info");
        String s = "";
        List<String> l = new ArrayList<>(idMap.keySet());
        Collections.sort(l, Collator.getInstance());
        for (String s1 : l) {
            s += s1 + Utils.LineSeparator;
        }
        try {
            Files.write(path, s.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
