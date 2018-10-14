package cz.neumimto.rpg.skills.mods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SkillModProcessorList {

	private Set<SkillModProcessor> processors = new HashSet<>();
	private Map<String, Float> nodes = new HashMap<>();

	public Set<SkillModProcessor> getProcessors() {
		return processors;
	}

	public Map<String, Float> getNodes() {
		return nodes;
	}
}
