package cz.neumimto.rpg.api.events.skill;

import cz.neumimto.rpg.api.skills.ISkill;

public interface SkillEvent {

    ISkill getSkill();

    void setSkill(ISkill iSkill);

}
