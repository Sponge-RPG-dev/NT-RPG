package cz.neumimto.model;

import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 16.9.2018.
 */
@JsBinding(JsBinding.Type.CLASS)
public class VitalizeEffectModel {

	public long duration;
	public long period;
	public float manaPerTick;
	public float healthPerTick;


	public VitalizeEffectModel() {

	}
}
