package cz.neumimto.rpg.skills.configs;

import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class SkillsDefinition {

	@Setting("Skills")
	private List<ScriptSkillModel> skills = new ArrayList<>();

	public List<ScriptSkillModel> getSkills() {
		return skills;
	}
}
