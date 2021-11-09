package cz.neumimto.rpg.common.configuration;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.HashSet;
import java.util.Set;


public class SkillDumpConfiguration {

    @Path("skillId")
    private String skillId;

    @Path("settings")
    private Set<String> floatNodes;

    public SkillDumpConfiguration() {
        this.floatNodes = new HashSet<>();
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public Set<String> getFloatNodes() {
        return floatNodes;
    }
}
