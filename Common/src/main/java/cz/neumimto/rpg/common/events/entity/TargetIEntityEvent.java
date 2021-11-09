package cz.neumimto.rpg.common.events.entity;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.effect.TargetIEffectConsumer;

/**
 * Base event for when a {@link IEntity} is a target.
 */
public interface TargetIEntityEvent extends TargetIEffectConsumer {

    void setTarget(IEntity iEntity);

}
