package cz.neumimto.rpg.sponge.skills.types;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.SkillScriptContext;
import cz.neumimto.rpg.api.skills.scripting.TargetedScriptExecutorSkill;
import cz.neumimto.rpg.api.skills.types.ITargetedScriptSkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;

import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class TargetedScriptSkill extends Targeted implements ITargetedScriptSkill {

    private TargetedScriptExecutorSkill executor;

    private ScriptSkillModel model;

    @Override
    public void castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext info, SkillContext skillContext) {
        SkillScriptContext context = new SkillScriptContext(this, info);
        executor.cast(source, target, skillContext, context);
        context.setResult(context.getResult() == null ? SkillResult.OK : context.getResult());
        skillContext.next(source, info, context.getResult());
    }

    @Override
    public void setExecutor(TargetedScriptExecutorSkill ses) {
        this.executor = ses;
    }

    @Override
    public ScriptSkillModel getModel() {
        return model;
    }

    @Override
    public void setModel(ScriptSkillModel model) {
        this.model = model;
        setLore(model.getLore());
        setDamageType(model.getDamageType());
        setDescription(model.getDescription());
        setLore(model.getLore());
        setDamageType(model.getDamageType());
        setDescription(model.getDescription());
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
