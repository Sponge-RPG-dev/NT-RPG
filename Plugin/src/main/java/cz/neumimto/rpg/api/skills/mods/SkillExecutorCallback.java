package cz.neumimto.rpg.api.skills.mods;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

/**
 * Created by NeumimTo on 27.10.2018.
 * <p>
 * Always last in the skill executor chain, may not call chain next
 */
public class SkillExecutorCallback extends ActiveSkillPreProcessorWrapper {

    public SkillExecutorCallback() {
        super(PreProcessorTarget.CALLBACK);
    }

    @Override
    public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {

    }
}
