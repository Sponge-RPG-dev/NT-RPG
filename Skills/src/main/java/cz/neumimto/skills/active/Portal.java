package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.PortalEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillContext;

/**
 * Created by NeumimTo on 22.7.2017.
 */
@ResourceLoader.Skill("ntrpg:portal")
public class Portal extends ActiveSkill {

	@Inject
	private EffectService effectService;


	public void init() {
		super.init();
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.COOLDOWN, 100000, -500);
		settings.addNode(SkillNodes.MANACOST, 50, 15);
		settings.addNode("chance-to-fail", 80, -50);
		settings.addNode("manacost-per-tick", 20, 5);
		settings.addNode("portal-duration", 20, 20);
		settings.addNode("manacost-per-teleported-entity", 5, 7);
		setSettings(settings);
		addSkillType(SkillType.UTILITY);
		addSkillType(SkillType.TELEPORT);
	}

	@Override
	public void cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext modifier) {
		if (character.hasEffect(PortalEffect.name)) {
			effectService.removeEffect(PortalEffect.name, character, this);
			modifier.next(character, info, modifier.result(SkillResult.CANCELLED));
			return;
		}
		long duration = getLongNodeValue(info, SkillNodes.MANACOST);
		double manaPerTick = getDoubleNodeValue(info, "manacost-per-tick");
		double manaPerEntity = getDoubleNodeValue(info, "manacost-per-teleported-entity");
		double chanceToFail = getDoubleNodeValue(info, "chance-to-fail");
		PortalEffect portalEffect = new PortalEffect(character, duration, null,
				manaPerTick, manaPerEntity, 1750, chanceToFail, false);
		effectService.addEffect(portalEffect, character, this);
		modifier.next(character, info, modifier.result(SkillResult.OK));
	}


}
