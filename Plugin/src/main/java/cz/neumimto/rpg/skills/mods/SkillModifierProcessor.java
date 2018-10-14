package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import org.spongepowered.api.CatalogType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ja on 22.10.2016.
 */
public abstract class SkillModifierProcessor implements CatalogType {

	private final String name;
	private final Set<ModifierTargetExcution> targetExcutions;

	public SkillModifierProcessor(String name, ModifierTargetExcution... targetExcutions) {
		this.name = name.toLowerCase();
		this.targetExcutions = new HashSet<>(Arrays.asList(targetExcutions));
	}

	public abstract void process(IActiveCharacter iActiveCharacter, SkillModifier parent, ExtendedSkillInfo info);

	public Set<ModifierTargetExcution> allowedTargets() {
		return targetExcutions;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
    public String getId() {
		return "ntrpg:" + getName();
	}

}
