package cz.neumimto.model;

import cz.neumimto.rpg.effects.common.stacking.UnstackableEffectData;
import cz.neumimto.rpg.scripting.JsBinding;

@JsBinding(JsBinding.Type.CLASS)
public class ShadowRunModel implements UnstackableEffectData<ShadowRunModel> {

	public long duration;
	public double damage;
	public double attackmult;
	public float walkspeed;

	public ShadowRunModel(long duration, double damage, double attackmult, float walkspeed) {
		this.duration = duration;
		this.damage = damage;
		this.attackmult = attackmult;
		this.walkspeed = walkspeed;
	}

	public ShadowRunModel() {
	}

	@Override
	public int compareTo(ShadowRunModel o) {
		return 1;
	}
}
