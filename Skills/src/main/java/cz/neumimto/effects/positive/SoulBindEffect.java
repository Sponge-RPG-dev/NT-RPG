package cz.neumimto.effects.positive;

import cz.neumimto.EffectLocalization;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;

import java.util.Collections;
import java.util.Set;

/**
 * Created by NeumimTo on 6.2.2016.
 */
public class SoulBindEffect extends EffectBase implements IEffectContainer {

	private final IEffectConsumer target;

	public SoulBindEffect(IEffectConsumer caster, IEffectConsumer target) {
		this.target = target;
		setConsumer(caster);
		setExpireMessage(EffectLocalization.SOULBIND_EXPIRE);
	}

	public IEffectConsumer getTarget() {
		return target;
	}


	@Override
	public Set<SoulBindEffect> getEffects() {
		return Collections.singleton(this);
	}

	@Override
	public Object getStackedValue() {
		return null;
	}

	@Override
	public void setStackedValue(Object o) {

	}

	@Override
	public SoulBindEffect constructEffectContainer() {
		return this;
	}

}
