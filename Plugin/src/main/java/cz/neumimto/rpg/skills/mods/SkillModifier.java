package cz.neumimto.rpg.skills.mods;

/**
 * Created by fs on 20.10.16.
 */
public class SkillModifier {

	private SkillModifierProcessor processor;
	private ModifierTargetExcution target;

	public SkillModifier() {

	}

	public SkillModifierProcessor getProcessor() {
		return processor;
	}


	public ModifierTargetExcution getTarget() {
		return target;
	}

}
