package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.positive.Invisibility;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

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
		settings.addNode(SkillNodes.DURATION, 10, 10);
		addSkillType(SkillType.STEALTH);
		addSkillType(SkillType.MOVEMENT);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		Invisibility invisibility = new Invisibility(character, duration);
		effectService.addEffect(invisibility, character, this);
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}


}
