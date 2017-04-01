package cz.neumimto.rpg.effects;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 1.4.2017.
 */
public class EffectContainer<T extends IEffect> implements IEffectContainer<T> {

	final Set<T> effects = new HashSet<>();

	final String name;

	final boolean stackable;

	public EffectContainer(T t) {
		name = t.getName();
		effects.add(t);
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


}
