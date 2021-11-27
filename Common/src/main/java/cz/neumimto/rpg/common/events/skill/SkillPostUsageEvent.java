package cz.neumimto.rpg.common.events.skill;

import cz.neumimto.rpg.common.entity.IEntity;

/**
 * Created by NeumimTo on 7.8.2015.
 */

public interface SkillPostUsageEvent extends SkillEvent {

    void setCaster(IEntity iEntity);

    IEntity getCaster();

}