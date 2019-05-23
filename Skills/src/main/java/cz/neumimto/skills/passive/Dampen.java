package cz.neumimto.skills.passive;

import cz.neumimto.effects.positive.DampenEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:dampen")
public class Dampen extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public Dampen() {
		super(DampenEffect.name);
		settings.addNode("min-mana", 310, -5);
		addSkillType(SkillType.UTILITY);
	}

	@Override
	public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
		int totalLevel = info.getTotalLevel();
		double val = info.getSkillData().getSkillSettings().getLevelNodeValue("min-mana", totalLevel);
		DampenEffect eff = new DampenEffect(character, -1, val);
		effectService.addEffect(eff, this);
	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
		super.skillUpgrade(IActiveCharacter, level);
		PlayerSkillContext info = IActiveCharacter.getSkill(getId());
		int totalLevel = info.getTotalLevel();
		double val = info.getSkillData().getSkillSettings().getLevelNodeValue("min-mana", totalLevel);
		IEffectContainer<Double, DampenEffect> effect = IActiveCharacter.getEffect(DampenEffect.name);
		effect.updateValue(val, this);
	}
}
