package cz.neumimto.rpg.effects;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 1.4.2017.
 */
public class EffectContainer<K, T extends IEffect<K>> implements IEffectContainer<K, T> {

	final Set<T> effects = new HashSet<>();

	final String name;

	final boolean stackable;

	private K value;

	private EffectStackingStrategy<K> effectStackingStrategy;

	public EffectContainer(T t) {
		name = t.getName();
		effects.add(t);
		effectStackingStrategy = t.getEffectStackingStrategy();
		this.stackable = t.isStackable();
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

}
