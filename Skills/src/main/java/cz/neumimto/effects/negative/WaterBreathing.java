package cz.neumimto.effects.negative;

import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.effects.common.mechanics.RPGPotionEffect;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;

/**
 * Created by NeumimTo on 3.6.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which gives water_breathing potion effect to the target")
public class WaterBreathing extends RPGPotionEffect {

    public static final String name = "WaterBreathing";

    public WaterBreathing(IEffectConsumer consumer, long duration) {
        super(name, consumer, duration, PotionEffect.builder()
                .potionType(PotionEffectTypes.WATER_BREATHING)
                .particles(true)
                .duration((int) (20 * duration / 1000)));
    }
}

