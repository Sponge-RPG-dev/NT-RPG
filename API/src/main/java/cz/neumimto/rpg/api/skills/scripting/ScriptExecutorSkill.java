package cz.neumimto.rpg.api.skills.scripting;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;

@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface ScriptExecutorSkill {

    SkillResult cast(IActiveCharacter character, PlayerSkillContext info);

}
