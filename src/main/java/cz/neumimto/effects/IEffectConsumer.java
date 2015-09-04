package cz.neumimto.effects;

import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.api.potion.PotionEffectType;

import java.util.Collection;

/**
 * Created by NeumimTo on 17.1.2015.
 */
public interface IEffectConsumer {
    Collection<IEffect> getEffects();

    IEffect getEffect(Class<? extends IEffect> cl);

    boolean hasEffect(Class<? extends IEffect> cl);

    void addEffect(IEffect effect);

    void removeEffect(Class<? extends IEffect> cl);

    void addPotionEffect(PotionEffectType p, int amplifier, long duration);

    void addPotionEffect(PotionEffectType p, int amplifier, long duration, boolean partciles);

    void removePotionEffect(PotionEffectType type);

    boolean hasPotionEffect(PotionEffectType type);

    void removeAllTempEffects();

    void addPotionEffect(PotionEffect e);

    void sendMessage(String message);
}
