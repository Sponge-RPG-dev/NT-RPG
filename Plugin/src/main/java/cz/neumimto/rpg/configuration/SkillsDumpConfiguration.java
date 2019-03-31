package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by NeumimTo on 8.7.2018.
 */
@ConfigSerializable
public class SkillsDumpConfiguration {

	@Setting(value = "effect", comment = "List of avalaible effect and its config nodes")
	private Map<String, EffectDumpConfiguration> effects;

	@Setting(value = "skill", comment = "List of avalaible skill and its config nodes")
	private List<SkillDumpConfiguration> skills;

	public SkillsDumpConfiguration() {
		this.skills = new ArrayList<>();
		this.effects = new TreeMap<>();
	}

	public Map<String, EffectDumpConfiguration> getEffects() {
		return effects;
	}

	public List<SkillDumpConfiguration> getSkills() {
		return skills;
	}
}
