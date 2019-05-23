package cz.neumimto.rpg.api.skills.mods;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.players.IActiveCharacter;

public abstract class ActiveSkillPreProcessorWrapper {

    private PreProcessorTarget target;

    public ActiveSkillPreProcessorWrapper(PreProcessorTarget target) {
        this.target = target;
    }

    public PreProcessorTarget getTarget() {
        return target;
    }

    public abstract void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult);
}
