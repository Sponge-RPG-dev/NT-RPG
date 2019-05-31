package cz.neumimto.rpg.sponge.events.skill;

import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.entities.IEntity;

public class AbstractSkillEvent {
    private ISkill skill;

    public ISkill getSkill() {
        return skill;
    }

    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

}
