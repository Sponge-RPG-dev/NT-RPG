package cz.neumimto.rpg.skills.pipeline;

import cz.neumimto.rpg.IEntity;

import java.util.List;

public interface TargetProcessor {

    void find(IEntity caster, SkillExecutorContext skill, List<SkillAction> actions);
}
