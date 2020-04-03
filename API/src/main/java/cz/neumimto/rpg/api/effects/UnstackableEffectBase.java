package cz.neumimto.rpg.api.effects;

import cz.neumimto.rpg.api.entity.IEffectConsumer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class UnstackableEffectBase<VALUE> extends EffectBase<VALUE> implements IEffectContainer {

    public UnstackableEffectBase(String name, IEffectConsumer consumer) {
        super(name, consumer);
        setStackable(false, null);
    }

    @Override
    public Set<? extends EffectBase> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }

    @Override
    public Object getStackedValue() {
        return null;
    }

    @Override
    public void setStackedValue(Object o) {

    }

    @Override
    public void removeStack(IEffect iEffect) {

    }

    @Override
    public IEffectContainer<VALUE, IEffect<VALUE>> getEffectContainer() {
        return this;
    }

    @Override
    public <T extends IEffect<VALUE>> IEffectContainer<VALUE, T> constructEffectContainer() {
        return this;
    }

    @Override
    public void setEffectContainer(IEffectContainer<VALUE, IEffect<VALUE>> iEffectContainer) {

    }
}
