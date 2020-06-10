package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;

import java.util.Collection;

public interface TargetSelector {

    Collection<IEntity> findTargets(IActiveCharacter caster, PlayerSkillContext context);

}
