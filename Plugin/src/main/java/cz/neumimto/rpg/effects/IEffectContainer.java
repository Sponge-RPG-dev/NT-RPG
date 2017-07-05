package cz.neumimto.rpg.effects;

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
		t.onApply();
		updateStackedValue();

	}

	default void forEach(Consumer<T> consumer) {
		for (T t : getEffects()) {
			consumer.accept(t);
		}
	}

	default void updateStackedValue(){
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
		iEffect.onRemove();
	}

	@Override
	default IEffectSource getType() {
		return EffectSourceType.EFFECT;
	}
}
