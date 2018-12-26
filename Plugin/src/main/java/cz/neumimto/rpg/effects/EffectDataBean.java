package cz.neumimto.rpg.effects;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class EffectDataBean {

    @Setting
    private String effect;

    @Setting
    private EffectParams params;

    public EffectDataBean(String effect, EffectParams params) {
        this.effect = effect;
        this.params = params;
    }

    public EffectDataBean() {
        params = new EffectParams();
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public EffectParams getParams() {
        return params;
    }

    public void setParams(EffectParams params) {
        this.params = params;
    }
}
