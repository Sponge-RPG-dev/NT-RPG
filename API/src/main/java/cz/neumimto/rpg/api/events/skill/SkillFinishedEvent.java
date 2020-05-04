package cz.neumimto.rpg.api.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;

public interface SkillFinishedEvent extends SkillEvent {

    void setCaster(IEntity iEntity);

    IEntity getCaster();

}