package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.BurningPrescenseEffect;
import cz.neumimto.model.BPModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.ItemTypes;

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
		settings.addNode(SkillNodes.PERIOD, 1000, -10);
		settings.addNode(SkillNodes.RADIUS, 3, 0);
		settings.addNode(SkillNodes.DAMAGE, 5, 1);
		super.settings = settings;
		setDamageType(DamageTypes.FIRE);
		addSkillType(SkillType.AURA);
		addSkillType(SkillType.AOE);
		addSkillType(SkillType.ELEMENTAL);
		addSkillType(SkillType.FIRE);
		setIcon(ItemTypes.FIRE_CHARGE);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		if (character.hasEffect(BurningPrescenseEffect.name)) {
			effectService.removeEffectContainer(character.getEffect(BurningPrescenseEffect.name), character);
		} else {
			BPModel model = getBPModel(info, character);
			model.duration = -1;
			BurningPrescenseEffect eff = new BurningPrescenseEffect(character, -1, model);
			effectService.addEffect(eff, character, this);
		}
		return SkillResult.OK;
	}

	private BPModel getBPModel(ExtendedSkillInfo info, IActiveCharacter character) {
		BPModel model = new BPModel();
		model.period = getIntNodeValue(info, SkillNodes.PERIOD);
		model.radius = getLongNodeValue(info, SkillNodes.RADIUS);
		model.damage = getIntNodeValue(info, SkillNodes.DAMAGE);
		return model;
	}
}
