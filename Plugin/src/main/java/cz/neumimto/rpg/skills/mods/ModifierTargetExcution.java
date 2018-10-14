package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;

public enum ModifierTargetExcution {
    /**
     * Modifiers to apply before skill execution
     * @see cz.neumimto.rpg.skills.parents.ActiveSkill
     * @see cz.neumimto.rpg.skills.parents.AbstractSkill#onPreUse(IActiveCharacter)
     */
    BEFORE,
    /**
     * Modifiers to apply during config read
     * @see cz.neumimto.rpg.skills.ExtendedSkillInfo#ge
     */
    EXECUTION,
    AFTER
}
