package cz.neumimto.skills;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class SkillSettings {
    private Map<String, Float> settingsMap = new HashMap<>();
    private Map<String, String> objMap = new HashMap<>();
    public static final String bonus = "_levelbonus";


    public void addNode(SkillNode n, float val, float levelbonux) {
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

    public boolean hasNode(SkillNode node) {
        return hasNode(node.name());
    }

    public void addNode(String s, float val) {
        settingsMap.put(s, val);
    }

    public void addObjectNode(String k, String v) {
        objMap.put(k.toLowerCase(), v);
    }

    public String getObjectNode(SkillNode k) {
        return getObjectNode(k.name());
    }

    public String getObjectNode(String k) {
        return objMap.get(k);
    }


    public float getNodeValue(SkillNode n) {
        return getNodeValue(n.toString());
    }

    public float getNodeValue(String s) {
        return settingsMap.get(s);
    }

    public float getLevelNodeValue(SkillNode n, int level) {
        return getLevelNodeValue(n.toString(), level);
    }

    public float getLevelNodeValue(String s, int level) {
        return getNodeValue(s) + level * getNodeValue(s + bonus);
    }

    public Map<String, Float> getNodes() {
        return settingsMap;
    }
}
