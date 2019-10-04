package cz.neumimto.rpg.common.effects;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 8.7.2018.
 */
public class EffectDumpConfiguration {

    @Path("description")
    private String description;

    @Path("settingNodes")
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
