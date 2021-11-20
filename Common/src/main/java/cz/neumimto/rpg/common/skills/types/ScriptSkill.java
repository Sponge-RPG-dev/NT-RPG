package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.ISkillType;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;

import java.util.List;
import java.util.Optional;

public interface ScriptSkill {

    void setHandler(SkillScriptHandlers handler);

    ScriptSkillModel getModel();

    void setModel(ScriptSkillModel model);
    default void initFromModel() {
        setDamageType(getModel().damageType);
        setCatalogId(getModel().id);
        List<String> configTypes = getModel().skillTypes;
        for (String configType : configTypes) {
            Optional<ISkillType> skillType = Rpg.get().getSkillService().getSkillType(configType);
            if (skillType.isPresent()) {
                addSkillType(skillType.get());
            } else {
                Log.warn("Unknown skill type " + configType);
            }
        }
    }


    void setCatalogId(String id);
    void setDamageType(String type);
    void addSkillType(ISkillType type);

}
