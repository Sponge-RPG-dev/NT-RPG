package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.ISkillType;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 7.10.2018.
 */
public class PassiveScriptSkill extends PassiveSkill implements IPassiveScriptSkill {

    private SkillScriptHandlers.Passive handler;

    private ScriptSkillModel model;

    @Override
    public void applyEffect(PlayerSkillContext context, IActiveCharacter character) {
        handler.init(character, context, this);
    }


    @Override
    public void setHandler(SkillScriptHandlers handler) {
        this.handler = (SkillScriptHandlers.Passive) handler;
    }

    @Override
    public ScriptSkillModel getModel() {
        return model;
    }

    @Override
    public void setModel(ScriptSkillModel model) {
        this.model = model;
        initFromModel();
    }
}
