package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.skills.SkillResult;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class UncastableModProcessor extends ImmutableSkillModProcessor {

	public UncastableModProcessor() {
		super(ModTargetExcution.BEFORE);
	}

	@Override
	public void merge(SkillModList skillModList) {
		skillModList.setSkipExecutionWithResult(SkillResult.CANCELLED);
	}
}
