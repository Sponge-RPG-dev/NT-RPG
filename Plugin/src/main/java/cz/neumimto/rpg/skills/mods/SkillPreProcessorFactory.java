package cz.neumimto.rpg.skills.mods;

import com.typesafe.config.ConfigObject;
import org.spongepowered.api.CatalogType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ja on 22.10.2016.
 */
public abstract class SkillPreProcessorFactory implements CatalogType {

	private final String name;
	private final Set<PreProcessorTarget> targetExcutions;

	public SkillPreProcessorFactory(String name, PreProcessorTarget... targetExcutions) {
		this.name = name.toLowerCase();
		this.targetExcutions = new HashSet<>(Arrays.asList(targetExcutions));
	}

	public Set<PreProcessorTarget> allowedTargets() {
		return targetExcutions;
	}

	public abstract ActiveSkillPreProcessorWrapper parse(ConfigObject configObject);

	@Override
	public String getName() {
		return name;
	}

	@Override
    public String getId() {
		return "ntrpg:" + getName();
	}

}
