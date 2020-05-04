package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;

public interface IActiveSkill<T extends IActiveCharacter> {

    SkillResult cast(T character, PlayerSkillContext info);
}
