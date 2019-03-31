package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.*;

/**
 * Created by NeumimTo on 8.7.2018.
 */
@ConfigSerializable
public class SkillsDumpConfiguration {

	@Setting(value = "effects", comment = "List of avalaible effects and its config nodes")
	private Map<String, EffectDumpConfiguration> effects;

	@Setting(value = "skills", comment = "List of avalaible skills and its config nodes")
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
