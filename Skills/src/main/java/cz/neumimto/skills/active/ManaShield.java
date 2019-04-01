package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.ManaShieldEffect;
import cz.neumimto.model.ManaShieldEffectModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

@ResourceLoader.Skill("ntrpg:manashield")
public class ManaShield extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		setDamageType(null);
		settings.addNode("reduction", 10f, 11f);
		settings.addNode("reduction-manacost", 20f, -1f);
		settings.addNode(SkillNodes.DURATION, 30000, 500);
		addSkillType(SkillType.UTILITY);
		addSkillType(SkillType.PROTECTION);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		ManaShieldEffectModel manaShieldEffectModel = new ManaShieldEffectModel();
		manaShieldEffectModel.reduction = skillContext.getDoubleNodeValue("reduction");
		manaShieldEffectModel.reductionCost = skillContext.getDoubleNodeValue("reduction-manacost");
		ManaShieldEffect effect = new ManaShieldEffect(character, skillContext.getLongNodeValue(SkillNodes.DURATION), manaShieldEffectModel);
		effectService.addEffect(effect, this);
		skillContext.next(character, info, SkillResult.OK);
    }
}
