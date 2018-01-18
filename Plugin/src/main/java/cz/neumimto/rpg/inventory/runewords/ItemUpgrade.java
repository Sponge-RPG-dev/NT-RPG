package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.SocketType;

import java.util.HashMap;
import java.util.Map;

public class ItemUpgrade {

    String name;
    Map<String, EffectParams> map;
    Map<String, Integer> attributes;
    private SocketType socketType;

    public SocketType getSocketType() {
        return socketType;
    }

    public void setSocketType(SocketType socketType) {
        this.socketType = socketType;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public boolean hasAnyEffects() {
        return map != null;
    }

    public void addEffect(String k, EffectParams v) {
        if (map == null)
            map = new HashMap<>();
        map.put(k, v);
    }


    public void addAttribute(String k, EffectParams v) {
        if (map == null)
            map = new HashMap<>();
        map.put(k, v);
    }

    public Map<String, EffectParams> getMap() {
        return map;
    }

    public void setMap(Map<String, EffectParams> map) {
        this.map = map;
    }

    public Map<String, Integer> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Integer> attributes) {
        this.attributes = attributes;
    }
}
