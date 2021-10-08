package cz.neumimto.rpg.common.events.skill;

import cz.neumimto.rpg.common.skills.ISkill;

public interface SkillEvent {

    ISkill getSkill();

    void setSkill(ISkill iSkill);

}
