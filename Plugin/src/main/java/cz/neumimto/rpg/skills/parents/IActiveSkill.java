package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.mods.SkillContext;

public interface IActiveSkill {

    void cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext modifier);
}
