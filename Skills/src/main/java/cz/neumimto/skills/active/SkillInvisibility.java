package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.positive.Invisibility;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillContext;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@ResourceLoader.Skill("ntrpg:invisibility")
@ResourceLoader.ListenerClass
public class SkillInvisibility extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		setDamageType(null);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 10, 10);
		setSettings(settings);
		addSkillType(SkillType.STEALTH);
		addSkillType(SkillType.MOVEMENT);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillContext) {
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		Invisibility invisibility = new Invisibility(character, duration);
		effectService.addEffect(invisibility, character, this);
		return skillContext.next(character, info, SkillResult.OK);
	}


}
