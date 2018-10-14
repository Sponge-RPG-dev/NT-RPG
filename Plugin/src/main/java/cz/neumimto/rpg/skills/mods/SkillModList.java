package cz.neumimto.rpg.skills.mods;

import java.util.EnumMap;

/**
 * Created by fs on 20.10.16.
 */
public class SkillModList {

	private final EnumMap<ModTargetExcution, SkillModProcessor> cache = new EnumMap<>(ModTargetExcution.class);

	public SkillModList() {

	}

	public SkillModProcessor getProcessors(ModTargetExcution stage) {
		return cache.get(stage);
	}


}
