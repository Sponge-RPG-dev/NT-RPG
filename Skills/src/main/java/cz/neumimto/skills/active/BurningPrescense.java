package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.BurningPrescenseEffect;
import cz.neumimto.model.BPModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@ResourceLoader.Skill("ntrpg:burningprescense")
public class BurningPrescense extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
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
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		if (character.hasEffect(BurningPrescenseEffect.name)) {
			effectService.removeEffectContainer(character.getEffect(BurningPrescenseEffect.name), character);
		} else {
			BPModel model = getBPModel(skillContext);
			model.duration = -1;
			BurningPrescenseEffect eff = new BurningPrescenseEffect(character, -1, model);
			effectService.addEffect(eff, character, this);
		}

		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}

	private BPModel getBPModel(SkillContext skillContext) {
		BPModel model = new BPModel();
		model.period = skillContext.getIntNodeValue(SkillNodes.PERIOD);
		model.radius = skillContext.getLongNodeValue(SkillNodes.RADIUS);
		model.damage = skillContext.getIntNodeValue(SkillNodes.DAMAGE);
		return model;
	}
}
