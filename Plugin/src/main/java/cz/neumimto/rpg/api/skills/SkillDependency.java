package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.common.skills.SkillData;

public class SkillDependency {
    public final SkillData skillData;
    public final int minSkillLevel;

    public SkillDependency(SkillData skillData, int minSkillLevel) {
        this.skillData = skillData;
        this.minSkillLevel = minSkillLevel;
    }

    @Override
    public String toString() {
        return skillData.getSkillName()+"("+minSkillLevel+")";
    }
}
