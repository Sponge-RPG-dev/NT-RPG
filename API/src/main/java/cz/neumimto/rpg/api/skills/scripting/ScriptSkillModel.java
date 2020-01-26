package cz.neumimto.rpg.api.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptSkillModel {

    @Path("Id")
    private String id;

    @Path("Name")
    private String name;

    @Path("Parent")
    private String parent;

    @Path("Skill-Types")
    private List<String> skillTypes;

    @Path("Damage-Type")
    private String damageType;

    @Path("Lore")
    private List<String> lore;

    @Path("Description")
    private List<String> description;

    @Path("Settings")
    @Conversion(SettingsToMap.class)
    private Map<String, Float> settings;

    @Path("Loader")
    private String loader;

    @Path("Script")
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

    public List<String> getSkillTypes() {
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

    private static class SettingsToMap implements Converter<Map<String, Float>, Config> {

        @Override
        public Map<String, Float> convertToField(Config value) {
            Map<String, Float> f = new HashMap<>();
            if (value == null) {
                return f;
            }
            Map<String, Object> stringObjectMap = value.valueMap();
            for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
                f.put(stringObjectEntry.getKey(), ((Number)stringObjectEntry.getValue()).floatValue());
            }
            return f;
        }

        @Override
        public Config convertFromField(Map<String, Float> value) {
            return null;
        }
    }
}
