package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface ITargeted<T extends IActiveCharacter> {
    SkillResult castOn(IEntity target, T source, PlayerSkillContext info);
}
