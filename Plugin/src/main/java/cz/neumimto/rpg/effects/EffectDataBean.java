package cz.neumimto.rpg.effects;

public class EffectDataBean {
    private String effect;
    private EffectParams params = new EffectParams();

    public EffectDataBean(String effect, EffectParams params) {
        this.effect = effect;
        this.params = params;
    }

    public EffectDataBean() {
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
