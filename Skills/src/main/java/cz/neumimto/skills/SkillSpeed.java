package cz.neumimto.skills;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.positive.SpeedBoost;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@ResourceLoader.Skill
public class SkillSpeed extends ActiveSkill {

    @Inject
    private EffectService effectService;

    public SkillSpeed() {
        setName("Speed");
        setDamageType(null);
        setDescription(SkillLocalization.SKILL_SPEED_DESC);
        SkillSettings settings = new SkillSettings();
        settings.addNode(SkillNode.DURATION, 1000, 1500);
        settings.addNode(SkillNode.AMOUNT, 0.1f, 0.05f);
        setSettings(settings);
        getSkillTypes().add(SkillType.CANT_CAST_WHILE_SILENCED);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info) {
        long duration = (long) super.settings.getLevelNodeValue(SkillNode.DURATION,info.getLevel());
        float amount = super.settings.getLevelNodeValue(SkillNode.AMOUNT,info.getLevel());
        SpeedBoost sb = new SpeedBoost(character,duration,amount);
        effectService.addEffect(sb,character);
        return SkillResult.OK;
    }
}
