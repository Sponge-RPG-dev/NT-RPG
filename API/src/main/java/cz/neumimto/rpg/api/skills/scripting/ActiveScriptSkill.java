package cz.neumimto.rpg.api.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.types.ScriptSkill;

import javax.inject.Inject;
import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class ActiveScriptSkill extends ActiveSkill<IActiveCharacter> implements ScriptSkill<ScriptExecutorSkill> {

    @Inject
    private IScriptEngine scriptEngine;

    private ScriptSkillModel model;

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext context) {
        Bindings bindings = new SimpleBindings();
        bindings.put("_caster", character);
        bindings.put("_context", context);

        if (model.fncCast() == null) {
            return SkillResult.FAIL;
        }

        SkillResult result = (SkillResult) getScriptEngine().executeScript(model.fncCast(), character, context);
        return result == null ? SkillResult.OK : result;
    }

    public IScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    @Override
    public ScriptSkillModel getModel() {
        return model;
    }

    public void setModel(ScriptSkillModel model) {
        this.model = model;
        setDamageType(model.getDamageType());
        setCatalogId(model.getId());
        List<String> configTypes = model.getSkillTypes();

        if (configTypes != null) {
            for (String configType : configTypes) {
                Optional<ISkillType> skillType = Rpg.get().getSkillService().getSkillType(configType);
                if (skillType.isPresent()) {
                    addSkillType(skillType.get());
                } else {
                    Log.warn("Unknown skill type " + configType);
                }
            }
        }
    }
}
