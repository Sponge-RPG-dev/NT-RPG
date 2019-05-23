package cz.neumimto.skills.active;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 1.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:conductivity")
public class Conductivity extends ActiveSkill {

	public void init() {
		super.init();
		settings.addNode(SkillNodes.DURATION, 10000, 500);
		settings.addNode(SkillNodes.RADIUS, 10, 1);
		settings.addNode(SkillNodes.RANGE, 15, 1);
		addSkillType(SkillType.CURSE);
		addSkillType(SkillType.DECREASED_RESISTANCE);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext modifier) {

	}
}
