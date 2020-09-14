package cz.neumimto.rpg.sponge.model;

import cz.neumimto.rpg.api.effects.stacking.UnstackableEffectData;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class CriticalEffectModel implements UnstackableEffectData<CriticalEffectModel> {

    public int chance;
    public float mult;

    public CriticalEffectModel() {

    }

    public CriticalEffectModel(int chance, float mult) {
        this.chance = chance;
        this.mult = mult;
    }

    @Override
    public int compareTo(CriticalEffectModel o) {
        if (o == null) {
            return -1;
        }
        return (int) ((o.chance + o.mult) - (chance + mult));
    }
}
