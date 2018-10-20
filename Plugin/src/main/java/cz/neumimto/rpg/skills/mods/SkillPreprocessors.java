package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;

public class SkillPreprocessors {

    public static ActiveSkillPreProcessorWrapper NOT_CASTABLE = new ActiveSkillPreProcessorWrapper(PreProcessorTarget.EARLY) {
        @Override
        public void doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillResult) {
            skillResult.continueExecution(false).result(SkillResult.FAIL);
        }
    };

}
