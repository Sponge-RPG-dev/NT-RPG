package cz.neumimto.rpg.common.configuration;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.*;


public class SkillDumpConfiguration {

    @Path("skillId")
    private String skillId;

    @Path("settings")
    private List<String> nodes;

    public SkillDumpConfiguration() {
        nodes = new ArrayList<>();
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public void add(String node) {
        nodes.add(node);
        nodes.sort(Comparator.naturalOrder());
    }
}
