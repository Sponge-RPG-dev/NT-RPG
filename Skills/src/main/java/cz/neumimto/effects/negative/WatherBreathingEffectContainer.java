package cz.neumimto.effects.negative;

import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.EffectStackingStrategy;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectContainer;

import java.util.Set;

/**
 * Created by NeumimTo on 3.6.2017.
 */
public class WatherBreathingEffectContainer extends EffectContainer<Object, WatherBreathingEffect> {

	public WatherBreathingEffectContainer(WatherBreathingEffect effect) {
		super(effect);
	}


	@Override
	public void removeStack(WatherBreathingEffect iEffect) {
		if (getEffects().size() == 1) {
			super.removeStack(iEffect);
		}
	}
}
