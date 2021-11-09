package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

public interface ISkillExecutor {

    ISkillExecutor init(SkillData skillData);

    SkillResult execute(IActiveCharacter character, PlayerSkillContext playerSkillContext);
}
