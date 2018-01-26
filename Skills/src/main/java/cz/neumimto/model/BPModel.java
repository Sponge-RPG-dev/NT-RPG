package cz.neumimto.model;

import cz.neumimto.rpg.effects.common.stacking.UnstackableEffectData;

/**
 * Created by NeumimTo on 5.7.2017.
 */
public class BPModel implements UnstackableEffectData<BPModel> {

	public long period;
	public float damage;
	public float radius;
	public int duration;

	@Override
	public int compareTo(BPModel o) {
		if (o == null)
			return -1;

		return (int) ((int) (o.period + o.damage + o.radius) - (period + damage + radius));
	}
}
