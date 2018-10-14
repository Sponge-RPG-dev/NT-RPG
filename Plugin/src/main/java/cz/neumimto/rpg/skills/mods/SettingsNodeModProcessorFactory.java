package cz.neumimto.rpg.skills.mods;

import com.typesafe.config.ConfigObject;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SettingsNodeModProcessorFactory extends SkillModProcessorFactory {

	public SettingsNodeModProcessorFactory() {
		super("config_node_linear", ModTargetExcution.EXECUTION);
	}

	@Override
	public ImmutableSkillModProcessor parse(ConfigObject configObject) {
		String node = configObject.toConfig().getString("Node");
		float value = (float) configObject.toConfig().getDouble("Value");
		return new SkillNodeLinearModProcessor(node,value);
	}
}
