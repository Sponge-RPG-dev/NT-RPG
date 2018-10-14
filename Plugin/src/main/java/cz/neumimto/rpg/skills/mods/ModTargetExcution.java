package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;

public enum ModTargetExcution {
    /**
     * Modifiers to apply before skill execution
     * @see cz.neumimto.rpg.skills.parents.ActiveSkill
     * @see cz.neumimto.rpg.skills.parents.AbstractSkill#onPreUse(IActiveCharacter)
     */
    BEFORE,
    /**
     * Modifiers to apply during config read
     * @see cz.neumimto.rpg.skills.parents.ActiveSkill#cast(IActiveCharacter, ExtendedSkillInfo, SkillModList)
     */
    EXECUTION,
    /**
     *
     * @see cz.neumimto.rpg.skills.parents.AbstractSkill#onPreUse(IActiveCharacter)
     */
    AFTER
}
