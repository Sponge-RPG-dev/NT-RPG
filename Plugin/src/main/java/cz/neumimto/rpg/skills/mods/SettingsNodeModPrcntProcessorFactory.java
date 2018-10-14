package cz.neumimto.rpg.skills.mods;

import com.typesafe.config.ConfigObject;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SettingsNodeModPrcntProcessorFactory extends SkillModProcessorFactory {

	public SettingsNodeModPrcntProcessorFactory() {
		super("config_node_percentage", ModTargetExcution.EXECUTION);
	}

	@Override
	public ImmutableSkillModProcessor parse(ConfigObject configObject) {
		String node = configObject.toConfig().getString("Node");
		float value = (float) configObject.toConfig().getDouble("Value");
		return new SkillNodePercentageModProcessor(node,value);
	}
}
