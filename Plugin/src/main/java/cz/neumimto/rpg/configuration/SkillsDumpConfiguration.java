package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 8.7.2018.
 */
@ConfigSerializable
public class SkillsDumpConfiguration {

    @Setting(value="effects", comment="List of avalaible effects and its config nodes")
    private Map<String, EffectDumpConfiguration> effects = new HashMap<>();

    @Setting(value="effects", comment="List of avalaible effects and its config nodes")
    private Map<String, SkillDumpConfiguration> skills = new HashMap<>();

    public Map<String, EffectDumpConfiguration> getEffects() {
        return effects;
    }

    public Map<String, SkillDumpConfiguration> getSkills() {
        return skills;
    }
}
