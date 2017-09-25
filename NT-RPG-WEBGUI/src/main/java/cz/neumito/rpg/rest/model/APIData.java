package cz.neumito.rpg.rest.model;

import java.util.HashMap;
import java.util.Map;


public class APIData {
	private Map<String, Integer> allocatedSkillpoints = new HashMap<>();

	public Map<String, Integer> getAllocatedSkillpoints() {
		return allocatedSkillpoints;
	}

	public void setAllocatedSkillpoints(Map<String, Integer> allocatedSkillpoints) {
		this.allocatedSkillpoints = allocatedSkillpoints;
	}
}
