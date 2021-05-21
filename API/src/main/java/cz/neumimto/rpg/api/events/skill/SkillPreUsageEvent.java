

package cz.neumimto.rpg.api.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.Cancellable;

/**
 * Created by NeumimTo on 1.8.2015.
 */
public interface SkillPreUsageEvent extends SkillEvent, Cancellable {

    void setCaster(IEntity iEntity);

    IEntity getCaster();

}
