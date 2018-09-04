package cz.neumimto.rpg.skills.pipeline;

import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ISkill;

import java.util.Map;

public class SkillExecutorContext {

	private ISkill skill;
	private ExtendedSkillInfo skillInfo;
	private Map<String, Object> params;
	private Map<String, Object> cache;

	public SkillExecutorContext(ISkill skill, ExtendedSkillInfo skillInfo, Map<String, Object> params) {
		this.skill = skill;
		this.skillInfo = skillInfo;
		this.params = params;
	}

	public ISkill getSkill() {
		return skill;
	}

	public ExtendedSkillInfo getSkillInfo() {
		return skillInfo;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public Map<String, Object> getCache() {
		return cache;
	}

	public void setCache(Map<String, Object> cache) {
		this.cache = cache;
	}
}
