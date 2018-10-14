package cz.neumimto.rpg.skills.mods;

import com.typesafe.config.ConfigObject;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class UncastableModProcessorFactory extends SkillModProcessorFactory {

	public UncastableModProcessorFactory() {
		super("uncastable", ModTargetExcution.BEFORE);
	}

	@Override
	public ImmutableSkillModProcessor parse(ConfigObject configObject) {
		return new UncastableModProcessor();
	}


}
