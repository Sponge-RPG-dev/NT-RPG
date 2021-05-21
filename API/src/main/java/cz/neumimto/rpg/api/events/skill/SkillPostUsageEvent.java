

package cz.neumimto.rpg.api.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;

/**
 * Created by NeumimTo on 7.8.2015.
 */

public interface SkillPostUsageEvent extends SkillEvent {

    void setCaster(IEntity iEntity);

    IEntity getCaster();

}