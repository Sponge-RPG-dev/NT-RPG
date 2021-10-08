package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillResult;

public interface ISkillCastMechanic {

    default SkillResult processBefore(IActiveCharacter character, PlayerSkillContext context) {
        return SkillResult.OK;
    }

    default void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context) {
    }

    boolean isValidForContext(SkillData skillData);

    default void notifyFailure(IActiveCharacter character, PlayerSkillContext context) {
    }
}
