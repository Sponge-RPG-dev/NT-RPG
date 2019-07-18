package cz.neumimto.rpg.sponge.events.effects;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.events.effect.TargetEffectEvent;
import cz.neumimto.rpg.sponge.events.AbstractNEvent;
import org.spongepowered.api.event.GenericEvent;

public abstract class AbstractEffectEvent<T extends IEffect> extends AbstractNEvent implements TargetEffectEvent<T>, GenericEvent<T> {

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

    @Override
    public TypeToken<T> getGenericType() {
        return TypeToken.of(getEffectClass());
    }

}
