package cz.neumimto.rpg.skills.configs;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class SkillsDefinition {

    @Setting("Skills")
    private List<ScriptSkillModel> skills;

    public List<ScriptSkillModel> getSkills() {
        return skills;
    }
}
