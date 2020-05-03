package cz.neumimto.rpg.api.skills.scripting;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;

@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface ScriptExecutorSkill {

    void cast(IActiveCharacter character, PlayerSkillContext info, SkillScriptContext context);

}
