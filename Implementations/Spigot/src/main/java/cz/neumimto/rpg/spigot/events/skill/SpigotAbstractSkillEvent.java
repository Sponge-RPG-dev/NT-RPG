package cz.neumimto.rpg.spigot.events.skill;

import cz.neumimto.rpg.common.events.skill.SkillEvent;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.spigot.events.AbstractNEvent;

public abstract class SpigotAbstractSkillEvent extends AbstractNEvent implements SkillEvent {

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
