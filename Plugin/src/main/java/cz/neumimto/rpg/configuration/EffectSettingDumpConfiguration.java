package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;

/**
 * Created by NeumimTo on 8.7.2018.
 */
@ConfigSerializable
public class EffectSettingDumpConfiguration {
    private String description;
    private Map<String, String> settingNodes;

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
