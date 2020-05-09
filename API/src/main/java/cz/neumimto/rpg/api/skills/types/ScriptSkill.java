package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;

public interface ScriptSkill<T> {

    ScriptSkillModel getModel();

    void setModel(ScriptSkillModel model);

}
