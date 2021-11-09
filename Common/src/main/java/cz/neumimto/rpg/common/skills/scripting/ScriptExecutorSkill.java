package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;

@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface ScriptExecutorSkill {

    SkillResult cast(IActiveCharacter character, PlayerSkillContext info);

}
