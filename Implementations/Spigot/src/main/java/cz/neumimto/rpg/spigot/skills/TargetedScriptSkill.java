package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.common.skills.types.ITargetedScriptSkill;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class TargetedScriptSkill extends TargetedEntitySkill implements ITargetedScriptSkill {

    private ScriptSkillModel model;

    private SkillScriptHandlers.Targetted handler;

    @Override
    public SkillResult castOn(IEntity target, SpigotCharacter source, PlayerSkillContext skillContext) {
        SkillResult skillResult = handler.castOnTarget(source, skillContext, target, this);
        return skillResult == null ? SkillResult.OK : skillResult;
    }

    @Override
    public void setHandler(SkillScriptHandlers handler) {
        this.handler = (SkillScriptHandlers.Targetted) handler;
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
