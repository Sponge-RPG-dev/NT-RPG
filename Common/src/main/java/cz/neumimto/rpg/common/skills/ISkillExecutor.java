package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

public interface ISkillExecutor {

    ISkillExecutor init(SkillData skillData);

    SkillResult execute(ActiveCharacter character, PlayerSkillContext playerSkillContext);
}
