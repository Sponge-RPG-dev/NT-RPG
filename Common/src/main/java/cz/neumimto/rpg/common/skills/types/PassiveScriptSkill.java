package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;

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
