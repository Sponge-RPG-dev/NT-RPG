package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;

public class Incinerate extends ActiveSkill {
    @Override
    public SkillResult onPreUse(Object character, PlayerSkillContext esi) {
        return null;
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
        return null;
    }
}
