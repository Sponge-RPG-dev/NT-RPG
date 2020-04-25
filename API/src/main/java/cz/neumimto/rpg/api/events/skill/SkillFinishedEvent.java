package cz.neumimto.rpg.api.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.mods.SkillContext;

public interface SkillFinishedEvent  extends SkillEvent {

    SkillContext getSkillContext();

    void setSkillContext(SkillContext context);

    void setCaster(IEntity iEntity);

    IEntity getCaster();

}