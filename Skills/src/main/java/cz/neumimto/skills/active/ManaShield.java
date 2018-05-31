package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.ManaShieldEffect;
import cz.neumimto.model.ManaShieldEffectModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

@ResourceLoader.Skill
public class ManaShield extends ActiveSkill {

    @Inject
    private EffectService effectService;

    public ManaShield() {
        setName("ManaShield");
        setLore(SkillLocalization.SKILL_MANASHIELD_LORE);
        setDamageType(null);
        setDescription(SkillLocalization.SKILL_MANASHIELD_DESC);
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
        manaShieldEffectModel.duration = getLongNodeValue(info, SkillNodes.DURATION);
        manaShieldEffectModel.reduction = getDoubleNodeValue(info, "reduction");
        manaShieldEffectModel.reductionCost = getDoubleNodeValue(info, "reduction-manacost");
        ManaShieldEffect effect = new ManaShieldEffect(character, manaShieldEffectModel);
        effectService.addEffect(effect, character, this);
        return SkillResult.OK;
    }
}
