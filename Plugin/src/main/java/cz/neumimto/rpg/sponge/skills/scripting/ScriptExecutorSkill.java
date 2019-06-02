package cz.neumimto.rpg.sponge.skills.scripting;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface ScriptExecutorSkill {

    void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext modifier, SkillScriptContext context);

}
