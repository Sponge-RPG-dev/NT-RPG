package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;

@ResourceLoader.Skill("ntrpg:null_and_void")
public class NullAndVoidSkill extends ActiveSkill {
    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
        return SkillResult.OK;
    }

    @Override
    public SkillResult onPreUse(Object character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }
}
