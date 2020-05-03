package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillResult;

public interface ISkillCostMechanic {

    SkillResult processBefore(IActiveCharacter character, PlayerSkillContext context);

    void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context);

    boolean isValidForContext(SkillData skillData);
}
