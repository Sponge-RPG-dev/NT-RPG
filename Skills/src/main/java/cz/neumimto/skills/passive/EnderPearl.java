package cz.neumimto.skills.passive;

import cz.neumimto.effects.EnderPearlEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.PassiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;

/**
 * Created by NeumimTo on 7.8.17.
 */
@ResourceLoader.Skill("ntrpg:enderpearl")
public class EnderPearl extends PassiveSkill {


	public EnderPearl() {
		super(EnderPearlEffect.name);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.COOLDOWN, 7500, -150);
		addSkillType(SkillType.TELEPORT);
		addSkillType(SkillType.MOVEMENT);
	}

	@Override
	public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
		long cooldown = getLongNodeValue(info, SkillNodes.COOLDOWN);
		EnderPearlEffect effect = new EnderPearlEffect(character, -1L, cooldown);
		effectService.addEffect(effect, character, this);
	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
		super.skillUpgrade(IActiveCharacter, level);
		ExtendedSkillInfo skill = IActiveCharacter.getSkill(getId());
		long cooldown = getLongNodeValue(skill, SkillNodes.COOLDOWN);
		IEffectContainer<Long, EnderPearlEffect> container = IActiveCharacter.getEffect(EnderPearlEffect.name);
		container.updateValue(cooldown, this);
	}
}