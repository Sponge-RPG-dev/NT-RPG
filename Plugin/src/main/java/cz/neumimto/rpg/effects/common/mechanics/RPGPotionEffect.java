package cz.neumimto.rpg.effects.common.mechanics;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import org.spongepowered.api.effect.potion.PotionEffect;

/**
 * Created by NeumimTo on 12.8.2017.
 */
public abstract class RPGPotionEffect extends EffectBase {

    public RPGPotionEffect(String name, IEffectConsumer iEffectConsumer,
                           long duration,
                           PotionEffect.Builder pe) {
        super(name, iEffectConsumer);
        pe.duration((int) (20 * duration / 1000));
        getPotions().add(pe.build());
    }

}
