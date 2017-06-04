package cz.neumimto.effects.negative;

import cz.neumimto.rpg.effects.EffectContainer;

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
