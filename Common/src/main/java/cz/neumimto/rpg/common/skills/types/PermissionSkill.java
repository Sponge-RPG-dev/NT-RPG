package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillExecutionType;
import cz.neumimto.rpg.common.skills.SkillResult;

public class PermissionSkill extends AbstractSkill<IActiveCharacter> {

    @Override
    public SkillResult onPreUse(IActiveCharacter character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(IActiveCharacter, context);
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(IActiveCharacter, level, context);
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(IActiveCharacter, context);
    }

}
