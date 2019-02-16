package cz.neumimto.rpg.skills;

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
