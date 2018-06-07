package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

/**
 * Created by NeumimTo on 1.8.2017.
 */
//@ResourceLoader.Skill
public class Conductivity extends ActiveSkill {

	public Conductivity() {
		setName(SkillLocalization.CONDUCTIVITY_NAME);
		setDescription(SkillLocalization.CONDUCTIVITY_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 10000, 500);
		settings.addNode(SkillNodes.RADIUS, 10, 1);
		settings.addNode(SkillNodes.RANGE, 15, 1);
		super.settings = settings;
		addSkillType(SkillType.CURSE);
		addSkillType(SkillType.DECREASED_RESISTANCE);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		return null;
	}
}
