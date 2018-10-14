package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;

import java.util.EnumMap;

/**
 * Created by fs on 20.10.16.
 */
public class SkillModList {

	private final EnumMap<ModTargetExcution, ImmutableSkillModProcessor> cache = new EnumMap<>(ModTargetExcution.class);

	private final SkillSettings settings = new SkillSettings();

	private SkillResult skipExecutionWithResult;

	public SkillModList() {

	}

	public SkillResult getSkipExecutionWithResult() {
		return skipExecutionWithResult;
	}

	public void setSkipExecutionWithResult(SkillResult skipExecutionWithResult) {
		this.skipExecutionWithResult = skipExecutionWithResult;
	}

	public ImmutableSkillModProcessor getProcessors(ModTargetExcution stage) {
		return cache.get(stage);
	}

	public void addProcessor(ImmutableSkillModProcessor processor) {
		processor.merge(this);
		cache.put(processor.getModTargetExecution(), processor);
	}

	public SkillSettings getSettings() {
		return settings;
	}

}
