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
