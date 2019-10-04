package cz.neumimto.rpg.common.configuration;

import com.electronwill.nightconfig.core.conversion.Path;
import cz.neumimto.rpg.api.configuration.SkillDumpConfiguration;
import cz.neumimto.rpg.common.effects.EffectDumpConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by NeumimTo on 8.7.2018.
 */
public class SkillsDumpConfiguration {

    @Path("effects")
    private Map<String, EffectDumpConfiguration> effects;

    @Path("skills")
    private List<SkillDumpConfiguration> skills;

    public SkillsDumpConfiguration() {
        this.skills = new ArrayList<>();
        this.effects = new TreeMap<>();
    }

    public Map<String, EffectDumpConfiguration> getEffects() {
        return effects;
    }

    public List<SkillDumpConfiguration> getSkills() {
        return skills;
    }
}
