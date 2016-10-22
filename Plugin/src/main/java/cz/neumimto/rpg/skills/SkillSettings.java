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

package cz.neumimto.rpg.skills;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class SkillSettings {
    public static final String bonus = "_levelbonus";
    private Map<String, Float> settingsMap = new HashMap<>();
    private Map<String, String> objMap = new HashMap<>();

    public void addNode(ISkillNode n, float val, float levelbonux) {
        addNode(n.toString(), val, levelbonux);
    }

    public void addNode(String n, float val, float levelbonux) {
        addNode(n, val);
        addNode(n + bonus, levelbonux);
    }

    public boolean hasNode(String s) {
        return settingsMap.containsKey(s) || objMap.containsKey(s);
    }

    public Map.Entry<String, Float> getFloatNodeEntry(String entry) {
        for (Map.Entry<String, Float> stringFloatEntry : settingsMap.entrySet()) {
            if (stringFloatEntry.getKey().equalsIgnoreCase(entry))
                return stringFloatEntry;
        }
        return null;
    }

    public boolean hasNode(ISkillNode node) {
        return hasNode(node.name());
    }

    public void addNode(String s, float val) {
        settingsMap.put(s, val);
    }

    public void addObjectNode(String k, String v) {
        objMap.put(k.toLowerCase(), v);
    }

    public String getObjectNode(ISkillNode k) {
        return getObjectNode(k.name());
    }

    public String getObjectNode(String k) {
        return objMap.get(k);
    }


    public float getNodeValue(ISkillNode n) {
        return getNodeValue(n.toString());
    }

    public float getNodeValue(String s) {
        return settingsMap.get(s);
    }

    public float getLevelNodeValue(ISkillNode n, int level) {
        return getLevelNodeValue(n.toString(), level);
    }

    public float getLevelNodeValue(String s, int level) {
        return getNodeValue(s) + level * getNodeValue(s + bonus);
    }

    public Map<String, Float> getNodes() {
        return settingsMap;
    }
}
