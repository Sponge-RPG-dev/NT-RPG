package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.skills.scripting.TargetedScriptExecutorSkill;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public interface ITargetedScriptSkill extends ScriptSkill<TargetedScriptExecutorSkill> {

	@Override
	default String getTemplateName() {
		return "templates/targeted.js";
	}
}
