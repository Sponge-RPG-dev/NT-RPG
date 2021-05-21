

package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface ITargeted<T extends IActiveCharacter> {
    SkillResult castOn(IEntity target, T source, PlayerSkillContext info);
}
