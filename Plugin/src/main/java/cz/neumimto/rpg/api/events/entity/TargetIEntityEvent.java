package cz.neumimto.rpg.api.events.entity;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.effect.TargetIEffectConsumer;

/**
 * Base event for when a {@link IEntity} is a target.
 */
public interface TargetIEntityEvent extends TargetIEffectConsumer {

    void setTarget(IEntity iEntity);
}
