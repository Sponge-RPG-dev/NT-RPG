package cz.neumimto.rpg.sponge.skills.scripting;

import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;

/**
 * Created by NeumimTo on 12.8.2018.
 */
public class SkillScriptContext {

    private final ISkill skill;
    private final PlayerSkillContext skillInfo;
    private SkillResult result;

    public SkillScriptContext(ISkill skill, PlayerSkillContext skillInfo) {
        this.skill = skill;
        this.skillInfo = skillInfo;
    }

    public ISkill getSkill() {
        return skill;
    }

    public PlayerSkillContext getSkillInfo() {
        return skillInfo;
    }

    public SkillResult getResult() {
        return result;
    }

    public void setResult(SkillResult result) {
        this.result = result;
    }
}
