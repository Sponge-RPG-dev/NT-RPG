package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;

@ResourceLoader.Skill("ntrpg:null_and_void")
public class NullAndVoidSkill extends ActiveSkill {
    @Override
    public SkillResult cast(ActiveCharacter character, PlayerSkillContext info) {
        return SkillResult.OK;
    }

    @Override
    public SkillResult onPreUse(Object character, PlayerSkillContext esi) {
        return SkillResult.OK;
    }
}
