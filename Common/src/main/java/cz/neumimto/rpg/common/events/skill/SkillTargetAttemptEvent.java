package cz.neumimto.rpg.common.events.skill;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.Cancellable;

public interface SkillTargetAttemptEvent extends SkillEvent, Cancellable {

    IEntity getCaster();

    void setCaster(IEntity caster);

    IEntity getTarget();

    void setTarget(IEntity target);

}
