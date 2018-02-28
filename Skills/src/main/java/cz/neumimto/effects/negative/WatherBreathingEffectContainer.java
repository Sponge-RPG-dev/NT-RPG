package cz.neumimto.effects.negative;

import cz.neumimto.rpg.effects.EffectContainer;

/**
 * Created by NeumimTo on 3.6.2017.
 */
public class WatherBreathingEffectContainer extends EffectContainer<Object, WaterBreathing> {

	public WatherBreathingEffectContainer(WaterBreathing effect) {
		super(effect);
	}


	@Override
	public void removeStack(WaterBreathing iEffect) {
		if (getEffects().size() == 1) {
			super.removeStack(iEffect);
		}
	}
}
