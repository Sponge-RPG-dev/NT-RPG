package cz.neumimto.skills.passive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.DampenEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.PassiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@ResourceLoader.Skill("ntrpg:dampen")
public class Dampen extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public Dampen() {
		super(DampenEffect.name);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode("min-mana", 310, -5);
		super.settings = skillSettings;
		addSkillType(SkillType.UTILITY);
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

		double val = getDoubleNodeValue(IActiveCharacter.getSkill(getId()), "min-mana");
		IEffectContainer<Double, DampenEffect> effect = IActiveCharacter.getEffect(DampenEffect.name);
		effect.updateValue(val, this);
	}
}
