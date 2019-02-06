package cz.neumimto.skills.passive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.CriticalEffect;
import cz.neumimto.model.CriticalEffectModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.PassiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

/**
 * Created by ja on 6.7.2017.
 */
@ResourceLoader.Skill("ntrpg:critical")
public class Critical extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public Critical() {
		super(CriticalEffect.name);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode(SkillNodes.CHANCE, 10, 20);
		skillSettings.addNode(SkillNodes.MULTIPLIER, 10, 20);
		super.settings = skillSettings;
		setDamageType(NDamageType.MEELE_CRITICAL);
		addSkillType(SkillType.PHYSICAL);
	}

	@Override
	public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
		CriticalEffectModel model = getModel(info);
		CriticalEffect dodgeEffect = new CriticalEffect(character, -1, model);
		effectService.addEffect(dodgeEffect, character, this);
	}

	@Override
	public void skillUpgrade(IActiveCharacter character, int level) {
		PlayerSkillContext info = character.getSkill(getId());
		IEffectContainer<CriticalEffectModel, CriticalEffect> effect = character.getEffect(CriticalEffect.name);
		effect.updateValue(getModel(info), this);
		effect.updateStackedValue();
	}

	private CriticalEffectModel getModel(PlayerSkillContext info) {
		int totalLevel = info.getTotalLevel();
		int chance = (int) info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.CHANCE, totalLevel);
		float mult = info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.MULTIPLIER, totalLevel);
		return new CriticalEffectModel(chance, mult);
	}
}
