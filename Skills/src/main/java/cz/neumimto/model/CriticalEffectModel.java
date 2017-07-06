package cz.neumimto.model;

import cz.neumimto.rpg.effects.common.stacking.UnstackableEffectData;

/**
 * Created by NeumimTo on 6.7.2017.
 */
public class CriticalEffectModel implements UnstackableEffectData<CriticalEffectModel> {
	public int chance;
	public float mult;

	public CriticalEffectModel() {
	}

	public CriticalEffectModel(int chance, float mult) {
		this.chance = chance;
		this.mult = mult;
	}

	@Override
	public int compareTo(CriticalEffectModel o) {
		if (o == null) {
			return -1;
		}
		return (int) ((o.chance + o.mult) - (chance  + mult));
	}
}
