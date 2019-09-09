package cz.neumimto.rpg.spigot.events.effects;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.events.effect.TargetEffectEvent;
import cz.neumimto.rpg.spigot.events.AbstractNEvent;

public abstract class AbstractEffectEvent<T extends IEffect> extends AbstractNEvent implements TargetEffectEvent<T> {

    protected T effect;

    @Override
    public IEffectConsumer getTarget() {
        return effect.getConsumer();
    }

    @Override
    public T getEffect() {
        return effect;
    }

    @Override
    public void setEffect(T effect) {
        this.effect = effect;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getEffectClass() {
        return (Class<T>) effect.getClass();
    }

}
