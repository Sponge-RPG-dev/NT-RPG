package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.mods.SkillContext;

@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface ScriptExecutorSkill {

	void cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext modifier, SkillScriptContext context);

}
