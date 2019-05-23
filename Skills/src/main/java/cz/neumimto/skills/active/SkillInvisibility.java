package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.Invisibility;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:invisibility")
@ResourceLoader.ListenerClass
public class SkillInvisibility extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Override
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
		effectService.addEffect(invisibility, this);
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}


}
