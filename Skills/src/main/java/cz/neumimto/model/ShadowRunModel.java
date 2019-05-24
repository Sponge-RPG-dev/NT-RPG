package cz.neumimto.model;

import cz.neumimto.rpg.api.effects.stacking.UnstackableEffectData;
import cz.neumimto.rpg.common.scripting.JsBinding;

@JsBinding(JsBinding.Type.CLASS)
public class ShadowRunModel implements UnstackableEffectData<ShadowRunModel> {

	public double damage;
	public double attackmult;
	public float walkspeed;

	public ShadowRunModel(double damage, double attackmult, float walkspeed) {
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
