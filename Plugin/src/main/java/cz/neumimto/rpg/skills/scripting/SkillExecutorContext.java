package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ISkill;

import java.util.Map;

public class SkillExecutorContext {

    private ISkill skill;
    private ExtendedSkillInfo skillInfo;
    private Map<String, Object> params;
    public SkillExecutorContext(ISkill skill, ExtendedSkillInfo skillInfo, Map<String, Object> params) {
        this.skill = skill;
        this.skillInfo = skillInfo;
        this.params = params;
    }

    public ISkill getSkill() {
        return skill;
    }

    public ExtendedSkillInfo getSkillInfo() {
        return skillInfo;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
