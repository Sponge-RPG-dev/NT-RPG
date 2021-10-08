package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;

public interface ScriptSkill<T> {

    void setHandler(SkillScriptHandlers handler);

    ScriptSkillModel getModel();

    void setModel(ScriptSkillModel model);

}
