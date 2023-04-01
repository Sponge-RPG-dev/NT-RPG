package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillResult;

public interface ISkillCastMechanic {

    default SkillResult processBefore(ActiveCharacter character, PlayerSkillContext context) {
        return SkillResult.OK;
    }

    default void processAfterSuccess(ActiveCharacter character, PlayerSkillContext context) {
    }

    boolean isValidForContext(SkillData skillData);

    default void notifyFailure(ActiveCharacter character, PlayerSkillContext context) {
    }
}
