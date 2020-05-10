package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.api.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;

public interface ScriptSkill<T> {

    void setHandler(SkillScriptHandlers handler);

    ScriptSkillModel getModel();

    void setModel(ScriptSkillModel model);

}
