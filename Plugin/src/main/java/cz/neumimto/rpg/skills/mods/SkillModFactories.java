package cz.neumimto.rpg.skills.mods;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SkillModFactories {

	public static UncastableModProcessorFactory UNCASTABLE = new UncastableModProcessorFactory();
	public static SettingsNodeModProcessorFactory SETTINGS_NODE_LINEAR = new SettingsNodeModProcessorFactory();
	public static SettingsNodeModPrcntProcessorFactory SETTINGS_NODE_PERCENTAGE = new SettingsNodeModPrcntProcessorFactory();

}
