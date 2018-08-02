package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.skills.ISkill;

import java.util.List;

public interface FindSkillTargetProcessor {

    List<IEntity> find(IEntity caster, SkillExecutorContext skill);
}
