package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.api.effects.EffectStackingStrategy;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectSource;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 1.4.2017.
 */
public interface IEffectContainer<K, T extends IEffect<K>> extends IEffectSourceProvider {

	Set<T> getEffects();

	String getName();

	boolean isStackable();

	default void mergeWith(IEffectContainer<K, T> IEffectContainer) {
		getEffects().addAll(IEffectContainer.getEffects());
		updateStackedValue();
	}

	default void stackEffect(T t, IEffectSourceProvider effectSourceProvider) {
		getEffects().add(t);
		t.onApply(t);
		updateStackedValue();
	}

	default void forEach(Consumer<T> consumer) {
		for (T t : getEffects()) {
			consumer.accept(t);
		}
	}

	default void updateStackedValue() {
		if (getEffectStackingStrategy() != null) {
			setStackedValue(getEffectStackingStrategy().getDefaultValue());
			for (T t : getEffects()) {
				setStackedValue(t.getEffectStackingStrategy().mergeValues(getStackedValue(), t.getValue()));
			}
		}
	}

	EffectStackingStrategy<K> getEffectStackingStrategy();

	K getStackedValue();

	void setStackedValue(K k);

	default void removeStack(T iEffect) {
		getEffects().remove(iEffect);
		if (iEffect.getConsumer() != null) {
			iEffect.onRemove(iEffect);
		}
	}

	default void updateValue(K value, IEffectSourceProvider provider) {
		for (T t : getEffects()) {
			if (t.getEffectSourceProvider() == provider) {
				t.setValue(value);
				break;
			}
		}
		updateStackedValue();
	}

	@Override
	default IEffectSource getType() {
		return EffectSourceType.EFFECT;
	}
}
