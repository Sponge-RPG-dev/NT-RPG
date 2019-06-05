package cz.neumimto.rpg.api.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.types.ScriptSkill;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.skills.scripting.ScriptExecutorSkill;

import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class ActiveScriptSkill extends ActiveSkill implements ScriptSkill<ScriptExecutorSkill> {

    private ScriptExecutorSkill executor;

    private ScriptSkillModel model;

    @Override
    public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext chain) {
        SkillScriptContext context = new SkillScriptContext(this, info);
        executor.cast(character, info, chain, context);
        SkillResult skillResult = context.getResult() == null ? SkillResult.OK : context.getResult();
        chain.next(character, info, chain.result(skillResult));
    }

    @Override
    public void setExecutor(ScriptExecutorSkill ses) {
        this.executor = ses;
    }

    @Override
    public ScriptSkillModel getModel() {
        return model;
    }

    public void setModel(ScriptSkillModel model) {
        this.model = model;
        setLore(model.getLore());
        setDamageType(model.getDamageType());
        setDescription(model.getDescription());
        setLocalizableName(model.getName());
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

    @Override
    public String getTemplateName() {
        return "templates/active.js";
    }
}
