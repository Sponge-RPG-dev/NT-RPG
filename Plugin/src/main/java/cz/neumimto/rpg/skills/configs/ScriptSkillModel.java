package cz.neumimto.rpg.skills.configs;

import cz.neumimto.rpg.skills.ISkillType;
import cz.neumimto.rpg.skills.tree.SkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class ScriptSkillModel {

	@Setting("Id")
	private String id;

	@Setting("Name")
	private Text name;

	@Setting("Parent")
	private String parent;

	@Setting("Skill-Types")
	private List<ISkillType> skillTypes;

	@Setting("Damage-Type")
	private DamageType damageType;

	@Setting("Lore")
	private List<Text> lore;

	@Setting("Description")
	private List<Text> description;

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

	public Text getName() {
		return name;
	}

	public void setName(Text name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<ISkillType> getSkillTypes() {
		return skillTypes;
	}

	public void setSkillTypes(List<ISkillType> skillTypes) {
		this.skillTypes = skillTypes;
	}

	public DamageType getDamageType() {
		return damageType;
	}

	public void setDamageType(DamageType damageType) {
		this.damageType = damageType;
	}

	public List<Text> getLore() {
		return lore;
	}

	public void setLore(List<Text> lore) {
		this.lore = lore;
	}

	public List<Text> getDescription() {
		return description;
	}

	public void setDescription(List<Text> description) {
		this.description = description;
	}

	public Map<String, Float> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, Float> settings) {
		this.settings = settings;
	}

	public String getLoader() {
		return loader;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
}
