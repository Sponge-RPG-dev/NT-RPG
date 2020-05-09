package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.scripting.PassiveScriptSkillHandler;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 7.10.2018.
 */
public class PassiveScriptSkill extends PassiveSkill implements IPassiveScriptSkill {


    private ScriptSkillModel model;

    @Override
    public void applyEffect(PlayerSkillContext context, IActiveCharacter character) {
        Bindings bindings = new SimpleBindings();
        bindings.put("_context", context);
        bindings.put("_caster", character);

    }


    @Override
    public ScriptSkillModel getModel() {
        return model;
    }

    @Override
    public void setModel(ScriptSkillModel model) {
        this.model = model;
        setDamageType(model.getDamageType());
        setCatalogId(model.getId());
        List<String> configTypes = model.getSkillTypes();
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
