package cz.neumimto.rpg.api.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptSkillModel {

    @Path("Id")
    private String id;

    @Path("Parent")
    private String parent;

    @Path("Skill-Types")
    private List<String> skillTypes;

    @Path("Damage-Type")
    private String damageType;

    @Path("Settings")
    @Conversion(SettingsToMap.class)
    private Map<String, Float> settings;

    @Path("Loader")
    private String loader;

    @Path("OnExecute")
    private String onExecute;

    @Path("OnLoad")
    @Optional
    private String onLoad;

    public String getScript(){
        return "";
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public List<String> getSkillTypes() {
        return skillTypes;
    }

    public String getDamageType() {
        return damageType;
    }

    public Map<String, Float> getSettings() {
        return settings;
    }

    public String getLoader() {
        return loader;
    }


    private static class SettingsToMap implements Converter<Map<String, Float>, Config> {

        @Override
        public Map<String, Float> convertToField(Config value) {
            Map<String, Float> f = new HashMap<>();
            if (value == null) {
                return f;
            }
            Map<String, Object> stringObjectMap = value.valueMap();
            for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
                f.put(stringObjectEntry.getKey(), ((Number) stringObjectEntry.getValue()).floatValue());
            }
            return f;
        }

        @Override
        public Config convertFromField(Map<String, Float> value) {
            return null;
        }
    }
}
