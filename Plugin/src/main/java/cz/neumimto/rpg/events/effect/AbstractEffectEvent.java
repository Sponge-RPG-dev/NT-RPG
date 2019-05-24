package cz.neumimto.rpg.events.effect;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.events.AbstractNEvent;
import org.spongepowered.api.event.GenericEvent;

public abstract class AbstractEffectEvent<T extends IEffect> extends AbstractNEvent implements TargetIEffectConsumer, GenericEvent<T> {
    protected final T effect;

    public AbstractEffectEvent(T effect) {
        this.effect = effect;
    }

    @Override
    public IEffectConsumer getTarget() {
        return effect.getConsumer();
    }

    public T getEffect() {
        return effect;
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
