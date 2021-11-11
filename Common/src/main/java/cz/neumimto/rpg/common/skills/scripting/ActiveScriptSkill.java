package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.types.ScriptSkill;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class ActiveScriptSkill extends ActiveSkill<IActiveCharacter> implements ScriptSkill {

    private SkillScriptHandlers.Active handler;

    private ScriptSkillModel model;

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext context) {
        SkillResult result = handler.onCast(character, context, this);
        return result == null ? SkillResult.OK : result;
    }

    @Override
    public void setHandler(SkillScriptHandlers handler) {
        this.handler = (SkillScriptHandlers.Active) handler;
    }

    @Override
    public ScriptSkillModel getModel() {
        return model;
    }

    public void setModel(ScriptSkillModel model) {
        this.model = model;
        initFromModel();
    }
}
