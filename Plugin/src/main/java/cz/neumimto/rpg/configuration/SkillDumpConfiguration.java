package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

@ConfigSerializable
public class SkillDumpConfiguration {
    private String skillId;
    private Set<String> floatNodes = new HashSet<>();

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
