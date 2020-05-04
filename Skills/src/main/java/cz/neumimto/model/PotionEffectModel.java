package cz.neumimto.model;

import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 9.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class PotionEffectModel {

    public Map<PotionEffectType, Long> cooldowns;
    public Map<PotionEffectType, Long> nextUseTime;

    public PotionEffectModel() {
        cooldowns = new HashMap<>();
        nextUseTime = new HashMap<>();
    }

    public void mergeWith(PotionEffectModel that) {
        HashMap<PotionEffectType, Long> map3 = new HashMap<>(that.cooldowns);
        for (Map.Entry<PotionEffectType, Long> e : cooldowns.entrySet()) {
            map3.merge(e.getKey(), e.getValue(), Math::min);
        }
        cooldowns.forEach((k, v) -> map3.merge(k, v, Math::min));
    }
}
