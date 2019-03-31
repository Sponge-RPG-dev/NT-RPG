package cz.neumimto.effects.positive;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

import java.util.Collections;
import java.util.Set;

/**
 * Created by NeumimTo on 23.12.2015.
 */

@Generate(id = "name", description = "An effect which makes the target invisible (Not by potion effect but by packets")
public class Invisibility extends EffectBase implements IEffectContainer {

	public static String name = "Invisibility";

	public Invisibility(IEffectConsumer consumer, long duration) {
		super(name, consumer);
		setDuration(duration);
	}

	@Override
	public void onApply(IEffect self) {
		Living entity = getConsumer().getEntity();
		entity.offer(Keys.VANISH, true);
	}

	@Override
	public void onRemove(IEffect self) {
		Living entity = getConsumer().getEntity();
		entity.offer(Keys.VANISH, false);
	}

	@Override
	public IEffectContainer constructEffectContainer() {
		return this;
	}

	@Override
	public Set<Invisibility> getEffects() {
		return Collections.singleton(this);
	}

	@Override
	public Object getStackedValue() {
		return null;
	}

	@Override
	public void setStackedValue(Object o) {

	}
}
