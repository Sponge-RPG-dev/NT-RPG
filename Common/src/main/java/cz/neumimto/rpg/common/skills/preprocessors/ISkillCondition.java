package cz.neumimto.rpg.common.skills.preprocessors;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;

public interface ISkillCondition {

    boolean isValidForContext(SkillData skillData);

    boolean check(IActiveCharacter character, PlayerSkillContext context);

}
