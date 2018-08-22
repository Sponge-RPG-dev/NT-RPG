package cz.neumimto.rpg.effects.common.mechanics;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 27.4.17.
 */
@Generate(id = "name", description = "A component which enables default health regeneration")
public class DefaultHealthRegeneration extends EffectBase implements IEffectContainer {

	public static final String name = "DefaultHealthRegen";

	public DefaultHealthRegeneration(IEffectConsumer character, long duration, String value) {
		super(name, character);
	}

	@Override
	public Set<DefaultHealthRegeneration> getEffects() {
		return new HashSet<>(Collections.singletonList(this));
	}


	@Override
	public DefaultHealthRegeneration constructEffectContainer() {
		return this;
	}

	@Override
	public Object getStackedValue() {
		return null;
	}

	@Override
	public void setStackedValue(Object o) {

	}
}
