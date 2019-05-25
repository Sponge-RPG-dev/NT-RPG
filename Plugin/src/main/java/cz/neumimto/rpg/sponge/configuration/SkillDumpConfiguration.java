package cz.neumimto.rpg.sponge.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

@ConfigSerializable
public class SkillDumpConfiguration {

    @Setting
    private String skillId;

    @Setting
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
