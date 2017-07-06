package cz.neumimto.skills;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.CriticalEffect;
import cz.neumimto.effects.positive.DodgeEffect;
import cz.neumimto.model.CriticalEffectModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by ja on 6.7.2017.
 */
@ResourceLoader.Skill
public class Critical extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public Critical() {
		setName("Dodge");
		setLore(SkillLocalization.SKILL_DODGE_LORE);
		setDescription(SkillLocalization.SKILL_DODGE_DESC);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode(SkillNodes.CHANCE, 10, 20);
		skillSettings.addNode(SkillNodes.MULTIPLIER, 10, 20);
		super.settings = skillSettings;
		setDamageType(NDamageType.MEELE_CRITICAL);
	}

	@Override
	public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
		int chance = getIntNodeValue(info, SkillNodes.CHANCE);
		float mult = getFloatNodeValue(info, SkillNodes.MULTIPLIER);
		CriticalEffectModel model = new CriticalEffectModel(chance, mult);
		CriticalEffect dodgeEffect = new CriticalEffect(character, -1, model);
		effectService.addEffect(dodgeEffect, character, this);
	}

	@Override
	public void skillUpgrade(IActiveCharacter character, int level) {
		ExtendedSkillInfo info = character.getSkill(getName());
		float chance = getFloatNodeValue(info, SkillNodes.CHANCE);
		IEffectContainer<Float, DodgeEffect> effect = character.getEffect(DodgeEffect.name);
		for (DodgeEffect dodgeEffect : effect.getEffects()) {
			if (dodgeEffect.getEffectSourceProvider() == this) {
				dodgeEffect.setValue(chance);
				break;
			}
		}
		effect.updateStackedValue();
	}
}
