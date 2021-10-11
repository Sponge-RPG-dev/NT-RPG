package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;

public interface IActiveSkill<T extends IActiveCharacter> {

    SkillResult cast(T character, PlayerSkillContext info);
}
