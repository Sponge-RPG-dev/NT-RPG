package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

public interface ISkillExecutor {

    ISkillExecutor init(SkillData skillData);

    SkillResult execute(IActiveCharacter character, PlayerSkillContext playerSkillContext);
}
