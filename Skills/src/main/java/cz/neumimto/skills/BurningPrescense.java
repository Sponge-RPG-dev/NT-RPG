package cz.neumimto.skills;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.BurningPrescenseEffect;
import cz.neumimto.model.BurningpresenseModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@ResourceLoader.Skill
public class BurningPrescense extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public BurningPrescense() {
		setName(SkillLocalization.burningPrescense);
		setDescription(SkillLocalization.burningPrescense_desc);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.CHANCE, 0.1f, 0.005f);
		settings.addNode(SkillNodes.PERIOD, 2500, -100);
		settings.addNode(SkillNodes.RADIUS, 2500, -100);
		super.settings = settings;
		setDamageType(DamageTypes.FIRE);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		if (character.hasEffect(BurningPrescenseEffect.name)) {
			effectService.removeEffect(character.getEffect(BurningPrescenseEffect.name), character);
		} else {
			BurningpresenseModel model = getBPModel(info, character);
			model.duration = -1;
		}
		return SkillResult.OK;
	}

	private BurningpresenseModel getBPModel(ExtendedSkillInfo info, IActiveCharacter character) {
		BurningpresenseModel model = new BurningpresenseModel();
		model.period = getIntNodeValue(info, SkillNodes.PERIOD);
		model.radius = getLongNodeValue(info, SkillNodes.RADIUS);
		model.damage = getIntNodeValue(info, SkillNodes.DAMAGE);
		return model;
	}
}
