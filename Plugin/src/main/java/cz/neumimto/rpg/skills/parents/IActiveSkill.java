package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.mods.SkillContext;

public interface IActiveSkill {

    void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext modifier);
}
