package cz.neumimto.rpg.effects;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by ja on 1.4.2017.
 */
public interface IEffectContainer<T extends IEffect> {
	Set<T> getEffects();

	String getName();

	boolean isStackable();

	default void mergeWith(IEffectContainer<T> IEffectContainer) {
		getEffects().addAll(IEffectContainer.getEffects());
	}

	default void stackEffect(T t, IEffectSourceProvider effectSourceProvider) {
		getEffects().add(t);
	}

	default void forEach(Consumer<T> consumer) {
		for (T t : getEffects()) {
			consumer.accept(t);
		}
	}
}
