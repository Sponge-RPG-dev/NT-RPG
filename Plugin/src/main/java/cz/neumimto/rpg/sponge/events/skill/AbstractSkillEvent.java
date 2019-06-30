package cz.neumimto.rpg.sponge.events.skill;

import cz.neumimto.rpg.api.events.skill.SkillEvent;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.sponge.events.AbstractNEvent;

public class AbstractSkillEvent extends AbstractNEvent implements SkillEvent {

    private ISkill skill;

    @Override
    public ISkill getSkill() {
        return skill;
    }

    @Override
    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

}
