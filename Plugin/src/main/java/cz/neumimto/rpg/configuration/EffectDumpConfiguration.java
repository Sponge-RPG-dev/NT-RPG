package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 8.7.2018.
 */
@ConfigSerializable
public class EffectDumpConfiguration {

	@Setting
	private String description;

	@Setting
	private Map<String, String> settingNodes;

	public EffectDumpConfiguration() {
		this.settingNodes = new HashMap<>();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getSettingNodes() {
		return settingNodes;
	}

	public void setSettingNodes(Map<String, String> settingNodes) {
		this.settingNodes = settingNodes;
	}
}
