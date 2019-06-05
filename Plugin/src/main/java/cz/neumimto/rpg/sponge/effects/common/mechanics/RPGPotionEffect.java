package cz.neumimto.rpg.sponge.effects.common.mechanics;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import org.spongepowered.api.effect.potion.PotionEffect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 12.8.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public abstract class RPGPotionEffect extends SpongeEffectBase<Long> implements IEffectContainer<Long, RPGPotionEffect> {

    private Set<PotionEffect> potions;

    public RPGPotionEffect(String name, ISpongeEntity iEffectConsumer,
                           long duration,
                           PotionEffect.Builder pe) {
        super(name, iEffectConsumer);
        pe.duration((int) (20 * duration / 1000));
        potions = new HashSet<>();
        getPotions().add(pe.build());
    }

    @Override
    public void onApply(IEffect self) {
        for (PotionEffect potion : potions) {
            getConsumer().addPotionEffect(potion);
        }
    }

    @Override
    public void onRemove(IEffect self) {
        for (PotionEffect potion : potions) {
            getConsumer().removePotionEffect(potion.getType());
        }
    }

    public Set<PotionEffect> getPotions() {
        return potions;
    }

    @Override
    public Set<RPGPotionEffect> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }

    @Override
    public Long getStackedValue() {
        return getDuration();
    }

    @Override
    public void setStackedValue(Long aLong) {
        setDuration(aLong);
    }

    @Override
    public void stackEffect(RPGPotionEffect rpgPotionEffect, IEffectSourceProvider effectSourceProvider) {
        setStackedValue(getStackedValue() + rpgPotionEffect.getStackedValue());
    }

}
