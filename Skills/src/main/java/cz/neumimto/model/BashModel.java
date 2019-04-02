package cz.neumimto.model;

import cz.neumimto.rpg.effects.common.stacking.UnstackableEffectData;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class BashModel implements UnstackableEffectData<BashModel> {

	public long stunDuration;

	public int chance;

	public long cooldown;

	public double damage;

	public transient long lasttime;

	@Override
	public int compareTo(BashModel o) {
		if (o == null) {
			return -1;
		}
		return (int) (((o.damage + o.stunDuration) * o.chance) - ((damage + stunDuration) * chance));
	}
}
