package cz.neumimto.rpg.effects;

import java.util.HashMap;
import java.util.Map;

public class EffectDataBean {
    private String effect;
    private Map<String, EffectParams> params = new HashMap<>();

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public Map<String, EffectParams> getParams() {
        return params;
    }

    public void setParams(Map<String, EffectParams> params) {
        this.params = params;
    }
}
