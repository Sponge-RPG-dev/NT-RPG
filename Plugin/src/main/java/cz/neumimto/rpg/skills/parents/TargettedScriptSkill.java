package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.configs.ScriptSkillModel;
import cz.neumimto.rpg.skills.scripting.TargettedScriptExecutorSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.entity.living.Living;

import java.util.List;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class TargettedScriptSkill extends Targetted implements ITargettedScriptSkill {

    private TargettedScriptExecutorSkill executor;

    private ScriptSkillModel model;

    public void setModel(ScriptSkillModel model) {
        this.model = model;
        setLore(model.getLore());
        setDamageType(model.getDamageType());
        setDescription(model.getDescription());
        setLocalizableName(model.getName());
        List<SkillType> skillTypes = model.getSkillTypes();
        skillTypes.forEach(super::addSkillType);
    }

    @Override
    public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
        return null;
    }

    public void setExecutor(TargettedScriptExecutorSkill ses) {
        this.executor = ses;
    }

    @Override
    public ScriptSkillModel getModel() {
        return model;
    }


}
