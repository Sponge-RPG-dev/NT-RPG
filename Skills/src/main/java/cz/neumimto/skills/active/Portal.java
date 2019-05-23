package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.PortalEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 22.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:portal")
public class Portal extends ActiveSkill {

	@Inject
	private EffectService effectService;


	public void init() {
		super.init();
		settings.addNode(SkillNodes.COOLDOWN, 100000, -500);
		settings.addNode("chance-to-fail", 80, -50);
		settings.addNode("manacost-per-tick", 20, 5);
		settings.addNode("portal-duration", 20, 20);
		settings.addNode("manacost-per-teleported-entity", 5, 7);
		addSkillType(SkillType.UTILITY);
		addSkillType(SkillType.TELEPORT);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		if (character.hasEffect(PortalEffect.name)) {
			effectService.removeEffect(PortalEffect.name, character, this);
			skillContext.next(character, info, skillContext.result(SkillResult.CANCELLED));
			return;
		}
		long duration = skillContext.getLongNodeValue(SkillNodes.MANACOST);
		double manaPerTick = skillContext.getDoubleNodeValue("manacost-per-tick");
		double manaPerEntity = skillContext.getDoubleNodeValue("manacost-per-teleported-entity");
		double chanceToFail = skillContext.getDoubleNodeValue("chance-to-fail");
		PortalEffect portalEffect = new PortalEffect(character, duration, null,
				manaPerTick, manaPerEntity, 1750, chanceToFail, false);
		effectService.addEffect(portalEffect, this);
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}


}
