package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;

public interface IActiveSkill {

    void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext modifier);
}
