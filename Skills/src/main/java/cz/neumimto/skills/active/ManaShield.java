package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.ManaShieldEffect;
import cz.neumimto.model.ManaShieldEffectModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillModifier;

@ResourceLoader.Skill("ntrpg:manashield")
public class ManaShield extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		setDamageType(null);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode("reduction", 10f, 11f);
		skillSettings.addNode("reduction-manacost", 20f, -1f);
		skillSettings.addNode(SkillNodes.DURATION, 30000, 500);
		settings = skillSettings;
		addSkillType(SkillType.UTILITY);
		addSkillType(SkillType.PROTECTION);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		ManaShieldEffectModel manaShieldEffectModel = new ManaShieldEffectModel();
		manaShieldEffectModel.duration = getLongNodeValue(info, SkillNodes.DURATION, modifier);
		manaShieldEffectModel.reduction = getDoubleNodeValue(info, "reduction", modifier);
		manaShieldEffectModel.reductionCost = getDoubleNodeValue(info, "reduction-manacost", modifier);
		ManaShieldEffect effect = new ManaShieldEffect(character, manaShieldEffectModel);
		effectService.addEffect(effect, character, this);
		return SkillResult.OK;
	}
}
