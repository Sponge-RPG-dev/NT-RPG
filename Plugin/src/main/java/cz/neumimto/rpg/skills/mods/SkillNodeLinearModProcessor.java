package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.skills.SkillSettings;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SkillNodeLinearModProcessor extends ImmutableSkillModProcessor {

	protected final String node;
	protected final float value;

	public SkillNodeLinearModProcessor(String node, float value) {
		super(ModTargetExcution.EXECUTION);
		this.node = node.toLowerCase();
		this.value = value;
	}

	public String getNode() {
		return node;
	}

	public float getValue() {
		return value;
	}

	@Override
	public void merge(SkillModList mod) {
		SkillSettings settings = mod.getSettings();
		Float aFloat = settings.getNodes().get(node);
		if (aFloat == null) {
			settings.addNode(node, value);
		} else {
			settings.addNode(node, aFloat + value);
		}
	}
}
