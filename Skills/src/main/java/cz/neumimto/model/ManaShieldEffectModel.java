package cz.neumimto.model;

import cz.neumimto.rpg.scripting.JsBinding;

@JsBinding(JsBinding.Type.CLASS)
public class ManaShieldEffectModel {

	public long duration;
	public double reductionCost;
	public double reduction;
}
