

package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.skills.scripting.JsBinding;

/**
 * Created by NeumimTo on 26.7.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public enum SkillResult {
    OK,
    FAIL,
    CANCELLED,
    CASTER_SILENCED,
    NO_TARGET,
    NO_HP,
    NO_MANA,
    ON_COOLDOWN,
    WRONG_DATA,
    NEGATIVE_SKILL_LEVEL,
    NOT_ACTIVE_SKILL,
    OK_NO_COOLDOWN
}
