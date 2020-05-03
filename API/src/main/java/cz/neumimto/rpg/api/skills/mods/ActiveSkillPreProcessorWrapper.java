package cz.neumimto.rpg.api.skills.mods;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;

public abstract class ActiveSkillPreProcessorWrapper {

    private PreProcessorTarget target;

    public ActiveSkillPreProcessorWrapper(PreProcessorTarget target) {
        this.target = target;
    }

    public PreProcessorTarget getTarget() {
        return target;
    }

    public abstract void doNext(IActiveCharacter character, PlayerSkillContext info);
}
