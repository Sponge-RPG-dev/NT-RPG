package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ISkill;

/**
 * Created by NeumimTo on 12.8.2018.
 */
public class SkillScriptContext {

    private final ISkill skill;
    private final ExtendedSkillInfo skillInfo;

    public SkillScriptContext(ISkill skill, ExtendedSkillInfo skillInfo) {
        this.skill = skill;
        this.skillInfo = skillInfo;
    }

    public ISkill getSkill() {
        return skill;
    }

    public ExtendedSkillInfo getSkillInfo() {
        return skillInfo;
    }

}
