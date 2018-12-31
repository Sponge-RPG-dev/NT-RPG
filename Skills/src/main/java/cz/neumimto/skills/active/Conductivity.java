package cz.neumimto.skills.active;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

/**
 * Created by NeumimTo on 1.8.2017.
 */
@ResourceLoader.Skill("ntrpg:conductivity")
public class Conductivity extends ActiveSkill {

	public void init() {
		super.init();
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 10000, 500);
		settings.addNode(SkillNodes.RADIUS, 10, 1);
		settings.addNode(SkillNodes.RANGE, 15, 1);
		super.settings = settings;
		addSkillType(SkillType.CURSE);
		addSkillType(SkillType.DECREASED_RESISTANCE);
	}

	@Override
	public void cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext modifier) {

	}
}
