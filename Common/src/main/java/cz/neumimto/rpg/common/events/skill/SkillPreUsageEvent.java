package cz.neumimto.rpg.common.events.skill;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.Cancellable;

/**
 * Created by NeumimTo on 1.8.2015.
 */
public interface SkillPreUsageEvent extends SkillEvent, Cancellable {

    void setCaster(IEntity iEntity);

    IEntity getCaster();

}
