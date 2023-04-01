package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;

public interface IActiveSkill<T extends ActiveCharacter> {

    SkillResult cast(T character, PlayerSkillContext info);
}
