package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.skills.scripting.PassiveScriptSkillHandler;
import cz.neumimto.rpg.sponge.skills.scripting.SkillScriptContext;

import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 7.10.2018.
 */
public class PassiveScriptSkill extends PassiveSkill implements IPassiveScriptSkill {


    private PassiveScriptSkillHandler handler;

    private ScriptSkillModel model;

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
        handler.init(character, info, new SkillScriptContext(this, info));
    }

    @Override
    public void setExecutor(PassiveScriptSkillHandler ses) {
        this.handler = ses;
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
        setLocalizableName(model.getName());
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
}
