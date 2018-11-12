package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.skills.scripting.TargettedScriptExecutorSkill;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public interface ITargettedScriptSkill extends ScriptSkill<TargettedScriptExecutorSkill> {

	@Override
	default String getTemplateName() {
		return "templates/targetted.js";
	}
}
