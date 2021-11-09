package cz.neumimto.rpg.common.skills.processors;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;

public interface ISkillCondition {

    boolean isValidForContext(SkillData skillData);

    boolean check(IActiveCharacter character, PlayerSkillContext context);

}
