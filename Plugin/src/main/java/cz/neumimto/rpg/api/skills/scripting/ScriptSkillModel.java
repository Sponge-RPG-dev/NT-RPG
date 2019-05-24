package cz.neumimto.rpg.api.skills.scripting;

import cz.neumimto.rpg.api.skills.ISkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class ScriptSkillModel {

	@Setting("Id")
	private String id;

	@Setting("Name")
	private String name;

	@Setting("Parent")
	private String parent;

	@Setting("Skill-Types")
	private List<ISkillType> skillTypes;

	@Setting("Damage-Type")
	private String damageType;

	@Setting("Lore")
	private List<String> lore;

	@Setting("Description")
	private List<String> description;

	@Setting("Settings")
	private Map<String, Float> settings;

	@Setting("Loader")
	private String loader;

	@Setting("Script")
	private String script;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getParent() {
		return parent;
	}

	public List<ISkillType> getSkillTypes() {
		return skillTypes;
	}

	public String getDamageType() {
		return damageType;
	}

	public List<String> getLore() {
		return lore;
	}

	public List<String> getDescription() {
		return description;
	}

	public Map<String, Float> getSettings() {
		return settings;
	}

	public String getLoader() {
		return loader;
	}

	public String getScript() {
		return script;
	}
}
