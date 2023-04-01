package cz.neumimto.rpg.common.skills.conditions;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillCastCondition;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.processors.ISkillCondition;

import javax.inject.Singleton;

@Singleton
@AutoService(ISkillCondition.class)
public class EffectCondition implements ISkillCondition {

    @Override
    public boolean check(ActiveCharacter character, PlayerSkillContext context) {
        SkillCastCondition castCondition = context.getSkillData().getSkillCastConditions().get("HasEffect");
        return character.hasEffect(castCondition.getValue());
    }

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return skillData.getSkillCastConditions() != null && skillData.getSkillCastConditions().containsKey("HasEffect");
    }
}
