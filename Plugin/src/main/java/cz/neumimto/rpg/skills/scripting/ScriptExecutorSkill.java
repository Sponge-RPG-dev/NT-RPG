package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.utils.SkillModifier;

@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface ScriptExecutorSkill {

    SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier, SkillScriptContext context);

}
