package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.skills.SkillSettings;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SkillNodePercentageModProcessor extends SkillNodeLinearModProcessor {

	public SkillNodePercentageModProcessor(String node, float value) {
		super(node, value);
	}

	@Override
	public void merge(SkillModList modList) {
		SkillSettings settings = modList.getSettings();
		Float aFloat = settings.getNodes().get(node);
		if (aFloat == null) {
			settings.addNode(node, 0);
		} else {
			settings.addNode(node, aFloat*(value/100.0f));
		}
	}
}
