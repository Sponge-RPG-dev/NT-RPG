package cz.neumimto.model;

import cz.neumimto.rpg.api.skills.scripting.JsBinding;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class MultiboltModel {

	public int timesToHit;
	public double damage;

	public MultiboltModel(int timesToHit, double damage) {
		this.timesToHit = timesToHit;
		this.damage = damage;
	}
}
