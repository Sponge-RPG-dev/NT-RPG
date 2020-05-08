package cz.neumimto.rpg.sponge.skills.types;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.types.ITargetedScriptSkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class TargetedScriptSkill extends Targeted implements ITargetedScriptSkill {

    private ScriptSkillModel model;

    private CompiledScript compiledScript;

    @Override
    public SkillResult castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext skillContext) {

        Bindings bindings = new SimpleBindings();
        bindings.put("_target", target);
        bindings.put("_caster", source);
        bindings.put("_context", skillContext);
        try {
            SkillResult skillResult = (SkillResult) compiledScript.eval(bindings);
            return skillResult == null ? SkillResult.OK : skillResult;
        } catch (ScriptException e) {
            return SkillResult.OK;
        }
    }

    @Override
    public void setScript(CompiledScript compiledScript) {
        this.compiledScript = compiledScript;
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
