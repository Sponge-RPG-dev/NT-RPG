package cz.neumimto.rpg.common.events.skill;

import cz.neumimto.rpg.common.entity.IEntity;

public interface SkillFinishedEvent extends SkillEvent {

    void setCaster(IEntity iEntity);

    IEntity getCaster();

}