package cz.neumimto.rpg.common.skills.conditions;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillCastCondition;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.common.skills.preprocessors.ISkillCondition;

import javax.inject.Singleton;

@Singleton
public class EffectCondition implements ISkillCondition {

    @Override
    public boolean check(IActiveCharacter character, PlayerSkillContext context) {
        SkillCastCondition castCondition = context.getSkillData().getSkillCastConditions().get("HasEffect");
        return character.hasEffect(castCondition.getValue());
    }

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return skillData.getSkillCastConditions() != null && skillData.getSkillCastConditions().containsKey("HasEffect");
    }
}
