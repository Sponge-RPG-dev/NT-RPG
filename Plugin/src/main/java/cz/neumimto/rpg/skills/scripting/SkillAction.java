package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.IEntity;

public interface SkillAction {

    void process(IEntity caster, IEntity target, SkillPipelineContext context);
}
