package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillExecutionType;
import cz.neumimto.rpg.common.skills.SkillResult;

public class PermissionSkill extends AbstractSkill<ActiveCharacter> {

    @Override
    public SkillResult onPreUse(ActiveCharacter character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
    }

    @Override
    public void skillLearn(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(ActiveCharacter, context);
    }

    @Override
    public void skillUpgrade(ActiveCharacter ActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(ActiveCharacter, level, context);
    }

    @Override
    public void skillRefund(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(ActiveCharacter, context);
    }

}
