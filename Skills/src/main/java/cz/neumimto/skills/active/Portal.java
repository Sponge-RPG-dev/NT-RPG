package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.PortalEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

import javax.sound.sampled.Port;

/**
 * Created by NeumimTo on 22.7.2017.
 */
@ResourceLoader.Skill
public class Portal extends ActiveSkill {

	@Inject
	private EffectService effectService;


	public Portal() {
		setName(SkillLocalization.SKILL_PORTAL_NAME);
		setDescription(SkillLocalization.SKILL_PORTAL_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.COOLDOWN, 100000, -500);
		settings.addNode(SkillNodes.MANACOST, 50, 15);
		settings.addNode("chance-to-fail", 80, -50);
		settings.addNode("manacost-per-tick", 20, 5);
		settings.addNode("portal-duration", 20, 20);
		settings.addNode("manacost-per-teleported-entity", 5, 7);
		setSettings(settings);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		if (character.hasEffect(PortalEffect.name)) {
			effectService.removeEffect(PortalEffect.name, character, this);
			return SkillResult.CANCELLED;
		}
		long duration = getLongNodeValue(info, SkillNodes.MANACOST);
		double manaPerTick = getDoubleNodeValue(info, "manacost-per-tick");
		double manaPerEntity = getDoubleNodeValue(info, "manacost-per-teleported-entity");
		double chanceToFail = getDoubleNodeValue(info, "chance-to-fail");
		PortalEffect portalEffect = new PortalEffect(character,duration, null,
				manaPerTick,manaPerEntity,3000, chanceToFail,false);
		return SkillResult.OK;
	}


}
