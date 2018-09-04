package cz.neumimto.rpg.skills;

import cz.neumimto.rpg.effects.IGlobalEffect;

/**
 * Created by NeumimTo on 15.7.2018.
 */
public class PassiveSkillEffectData extends SkillData {

	private IGlobalEffect effect;
	private Class<?> model;

	public PassiveSkillEffectData(String skill) {
		super(skill);
	}

	public IGlobalEffect getEffect() {
		return effect;
	}

	public void setEffect(IGlobalEffect effect) {
		this.effect = effect;
	}

	public Class<?> getModel() {
		return model;
	}

	public void setModel(Class<?> model) {
		this.model = model;
	}
}
