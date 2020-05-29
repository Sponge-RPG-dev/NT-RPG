package cz.neumimto.rpg.reagents;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillResult;

public interface ISkillCastMechanic {

    default SkillResult processBefore(IActiveCharacter character, PlayerSkillContext context){
        return SkillResult.OK;
    }

    default void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context){}

    boolean isValidForContext(SkillData skillData);

    default void notifyFailure(IActiveCharacter character, PlayerSkillContext context) {}
}
