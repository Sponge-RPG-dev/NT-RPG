package cz.neumimto.rpg.common.skills.preprocessors;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.api.skills.mods.PreProcessorTarget;
import cz.neumimto.rpg.api.skills.mods.SkillContext;

public class SkillPreprocessors {

    public static ActiveSkillPreProcessorWrapper NOT_CASTABLE = new ActiveSkillPreProcessorWrapper(PreProcessorTarget.EARLY) {
        @Override
        public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
            skillResult.endWith(character, info, skillResult.result(SkillResult.FAIL));
        }
    };

    public static ActiveSkillPreProcessorWrapper SKILL_COST = new SkillCostPreprocessor();
}
