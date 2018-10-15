package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;

public abstract class ActiveSkillProcessorWrapper {

    private ModTargetExcution target;

    public ActiveSkillProcessorWrapper(ModTargetExcution target) {
        this.target = target;
    }

    public ModTargetExcution getTarget() {
        return target;
    }

    public abstract SkillResult doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillResult skillResult);
}
