package cz.neumimto.skills;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class ExtendedSkillInfo {
    public static ExtendedSkillInfo Empty = new ExtendedSkillInfo() {{
        setSkillInfo(SkillInfo.EMPTY);
    }};
    ISkill skill;
    int level;
    SkillInfo skillInfo;

    public ISkill getSkill() {
        return skill;
    }

    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public SkillInfo getSkillInfo() {
        return skillInfo;
    }

    public void setSkillInfo(SkillInfo skillInfo) {
        this.skillInfo = skillInfo;
    }
}
