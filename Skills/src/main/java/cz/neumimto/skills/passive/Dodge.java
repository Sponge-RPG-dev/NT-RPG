package cz.neumimto.skills.passive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.DodgeEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.parents.PassiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@ResourceLoader.Skill("ntrpg:dodge")
public class Dodge extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public Dodge() {
		super(DodgeEffect.name);
		settings.addNode(SkillNodes.CHANCE, 10, 20);
		addSkillType(SkillType.PHYSICAL);
	}

	@Override
	public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
		int totalLevel = info.getTotalLevel();
		float chance = info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.CHANCE, totalLevel);
		DodgeEffect dodgeEffect = new DodgeEffect(character, -1, chance);
		effectService.addEffect(dodgeEffect, character, this);
	}

	@Override
	public void skillUpgrade(IActiveCharacter character, int level) {
		PlayerSkillContext info = character.getSkill(getId());
		int totalLevel = info.getTotalLevel();
		float chance = info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.CHANCE, totalLevel);
		IEffectContainer<Float, DodgeEffect> effect = character.getEffect(DodgeEffect.name);
		effect.updateValue(chance, this);
	}
}
