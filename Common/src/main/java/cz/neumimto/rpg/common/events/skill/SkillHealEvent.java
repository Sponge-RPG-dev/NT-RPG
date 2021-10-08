

package cz.neumimto.rpg.common.events.skill;

import cz.neumimto.rpg.common.IRpgElement;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.Cancellable;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public interface SkillHealEvent extends SkillEvent, Cancellable {

    double getAmount();

    void setAmount(double amount);

    IRpgElement getSource();

    void setSource(IRpgElement element);

    IEntity getEntity();

    void setEntity(IEntity entity);

}
