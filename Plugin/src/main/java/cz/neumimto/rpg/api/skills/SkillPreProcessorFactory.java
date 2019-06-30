package cz.neumimto.rpg.api.skills;

import com.typesafe.config.ConfigObject;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.api.skills.mods.PreProcessorTarget;

import java.util.Set;

/**
 * Created by ja on 22.10.2016.
 */
public abstract class SkillPreProcessorFactory {

    private final String name;
    private final Set<PreProcessorTarget> targetExcutions;

    public SkillPreProcessorFactory(String name, Set<PreProcessorTarget> targetExcutions) {
        this.name = name.toLowerCase();
        this.targetExcutions = targetExcutions;
    }

    public Set<PreProcessorTarget> allowedTargets() {
        return targetExcutions;
    }

    public abstract ActiveSkillPreProcessorWrapper parse(ConfigObject configObject);

    public String getName() {
        return name;
    }

    public String getId() {
        return "ntrpg:" + getName();
    }

}
