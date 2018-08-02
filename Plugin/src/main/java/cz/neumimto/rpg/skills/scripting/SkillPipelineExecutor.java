package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.skills.ISkill;

public class SkillPipelineExecutor {
    private final IEntity caster;
    private final ISkill skill;

    public SkillPipelineExecutor(IEntity caster, ISkill skill) {
        this.caster = caster;
        this.skill = skill;
    }


}
