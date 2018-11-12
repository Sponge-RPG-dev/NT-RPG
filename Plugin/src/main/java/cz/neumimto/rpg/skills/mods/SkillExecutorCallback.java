package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;

/**
 * Created by NeumimTo on 27.10.2018.
 *
 * Always last in the skill executor chain, may not call chain next
 */
public class SkillExecutorCallback extends ActiveSkillPreProcessorWrapper {

    public SkillExecutorCallback() {
        super(PreProcessorTarget.CALLBACK);
    }

    @Override
    public void doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillResult) {

    }
}
