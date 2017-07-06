package cz.neumimto.skills;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.ResoluteTechniqueEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@ResourceLoader.Skill
public class ResoluteTechnique extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public ResoluteTechnique() {
		setName("Resolute Technique");
		setName("Multibolt");
		setLore(SkillLocalization.SKILL_RESOLUTE_TECHNIQUE_LORE);
		setDescription(SkillLocalization.SKILL_RESOLUTE_TECHNIQUE_DESC);
		super.settings = new SkillSettings();
	}

	@Override
	public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
		ResoluteTechniqueEffect effect = new ResoluteTechniqueEffect(character, -1, null);
		effectService.addEffect(effect, character, this);
	}
}
