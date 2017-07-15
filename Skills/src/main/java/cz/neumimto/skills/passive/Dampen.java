package cz.neumimto.skills.passive;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.DampenEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@ResourceLoader.Skill
public class Dampen extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public Dampen() {
		setName("Dampen");
		setLore(SkillLocalization.SKILL_DAMPEN_LORE);
		setDescription(SkillLocalization.SKILL_DAMPEN_DESC);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode("min-mana", 310, -5);
		super.settings = skillSettings;
	}

	@Override
	public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
		double val = getDoubleNodeValue(info, "min-mana");
		DampenEffect eff = new DampenEffect(character, -1, val);
		effectService.addEffect(eff, character, this);
	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
		super.skillUpgrade(IActiveCharacter, level);

		double val = getDoubleNodeValue(IActiveCharacter.getSkill(getName()), "min-mana");
		IEffectContainer<Double,DampenEffect> effect = IActiveCharacter.getEffect(DampenEffect.name);
		effect.updateValue(val, this);
	}
}
