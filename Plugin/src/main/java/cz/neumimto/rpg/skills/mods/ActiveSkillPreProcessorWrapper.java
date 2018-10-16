package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;

public abstract class ActiveSkillPreProcessorWrapper {

    private PreProcessorTarget target;

    public ActiveSkillPreProcessorWrapper(PreProcessorTarget target) {
        this.target = target;
    }

    public PreProcessorTarget getTarget() {
        return target;
    }

    public abstract SkillResult doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillResult skillResult);
}
