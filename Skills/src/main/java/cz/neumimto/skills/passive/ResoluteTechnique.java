package cz.neumimto.skills.passive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.ResoluteTechniqueEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.PassiveSkill;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@ResourceLoader.Skill("ntrpg:resolutetechnique")
public class ResoluteTechnique extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public ResoluteTechnique() {
		super(ResoluteTechniqueEffect.name);
		super.settings = new SkillSettings();
		setDamageType(DamageTypes.ATTACK);
		addSkillType(SkillType.PHYSICAL);
	}

	@Override
	public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
		ResoluteTechniqueEffect effect = new ResoluteTechniqueEffect(character, -1, null);
		effectService.addEffect(effect, character, this);
	}
}
