package cz.neumimto.rpg.skills.mods;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public abstract class ImmutableSkillModProcessor {
	private ModTargetExcution modTargetExecution;

	public ImmutableSkillModProcessor(ModTargetExcution modTargetExecution) {
		this.modTargetExecution = modTargetExecution;
	}

	public ModTargetExcution getModTargetExecution() {
		return modTargetExecution;
	}

	public abstract void merge(SkillModList skillModList);
}
