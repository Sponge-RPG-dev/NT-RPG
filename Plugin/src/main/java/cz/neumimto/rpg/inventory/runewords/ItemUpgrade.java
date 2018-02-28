package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.sockets.SocketType;
import cz.neumimto.rpg.inventory.sockets.SocketTypes;
import org.spongepowered.api.Sponge;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 21.1.2018.
 */
public class ItemUpgrade extends HashMap<String, Object> {

    public static final String NAME = "NAME";
    public static final String SOCKET_TYPE = "SOCKET_TYPE";
    public static final String TYPE = "TYPE";
    public static final String EFFECTS = "EFFECTS";
    public static final String ATTRIBUTES = "ATTRIBUTES";

    public ItemUpgrade() {
    }

    public ItemUpgrade(Map<? extends String, ?> m) {
        super(m);
    }

    public SocketType getSocketType() {
        String s = (String) get(SOCKET_TYPE);
        return s == null ? null : Sponge.getRegistry().getType(SocketType.class, s).get();
    }

    public void setSocketType(SocketType socketType) {
        put(SOCKET_TYPE, socketType.getId());
    }

    public String getName() {
        return (String) get(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public void addEffect(String k, EffectParams v) {
        Map<String, EffectParams> a = (Map<String, EffectParams>) get(EFFECTS);
        if (a == null) {
            a = new HashMap<>();
            put(EFFECTS, a);
        }
        a.put(k, v);
    }

    public Map<String, EffectParams> getEffects() {
        return (Map<String, EffectParams>) get(EFFECTS);
    }


}
