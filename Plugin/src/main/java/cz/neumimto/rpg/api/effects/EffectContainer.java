package cz.neumimto.rpg.api.effects;

import cz.neumimto.rpg.api.effects.stacking.UnstackableEffectData;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 1.4.2017.
 */
public class EffectContainer<K, T extends IEffect<K>> implements IEffectContainer<K, T> {

    final protected Set<T> effects = new HashSet<>();

    final String name;

    boolean stackable;

    private K value;

    private EffectStackingStrategy<K> effectStackingStrategy;

    public EffectContainer(T t) {
        name = t.getName();
        init(t);
    }

    protected void init(T t) {
        this.effects.add(t);
        this.effectStackingStrategy = t.getEffectStackingStrategy();
        this.stackable = t.isStackable();
        this.value = t.getValue();
    }

    @Override
    public Set<T> getEffects() {
        return effects;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isStackable() {
        return stackable;
    }

    @Override
    public EffectStackingStrategy<K> getEffectStackingStrategy() {
        return effectStackingStrategy;
    }

    @Override
    public K getStackedValue() {
        return value;
    }

    @Override
    public void setStackedValue(K k) {
        this.value = k;
    }


    public static class UnstackableSingleInstance extends EffectContainer<UnstackableEffectData<?>, IEffect<UnstackableEffectData<?>>> {

        public UnstackableSingleInstance(IEffect iEffect) {
            super(iEffect);
        }

        @Override
        public void stackEffect(IEffect<UnstackableEffectData<?>> unstackableEffectDataIEffect, IEffectSourceProvider effectSourceProvider) {
            super.stackEffect(unstackableEffectDataIEffect, effectSourceProvider);
        }

        @Override
        public void removeStack(IEffect<UnstackableEffectData<?>> iEffect) {
            super.removeStack(iEffect);
            updateStackedValue();
        }


        @Override
        public void updateStackedValue() {
            UnstackableEffectData stackedValue = getStackedValue();
            IEffect<UnstackableEffectData<?>> next = null;
            for (IEffect<UnstackableEffectData<?>> effect : getEffects()) {
                if (stackedValue.isInferiorTo(effect.getValue())) {
                    if (next != null) {
                        next.setTickingDisabled(true);
                    }
                    next = effect;
                    setStackedValue(effect.getValue());
                } else {
                    effect.setTickingDisabled(true);
                }
            }
        }
    }
}
