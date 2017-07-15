package cz.neumimto.model;

import cz.neumimto.rpg.effects.common.stacking.UnstackableEffectData;

/**
 * Created by NeumimTo on 5.7.2017.
 */
public class BurningpresenseModel implements UnstackableEffectData<BurningpresenseModel> {

	public long period;
	public float damage;
	public float radius;
	public int duration;

	@Override
	public int compareTo(BurningpresenseModel o) {
		if (o == null)
			return -1;

		return (int) ((int) (o.period + o.damage + o.radius) - (period + damage + radius));
	}
}
