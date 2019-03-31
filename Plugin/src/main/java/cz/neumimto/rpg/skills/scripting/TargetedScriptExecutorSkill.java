package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.mods.SkillContext;

/**
 * Created by NeumimTo on 3.9.2018.
 */
@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface TargetedScriptExecutorSkill {

	void cast(IActiveCharacter character, IEntity target, SkillContext modifier, SkillScriptContext context);
}
