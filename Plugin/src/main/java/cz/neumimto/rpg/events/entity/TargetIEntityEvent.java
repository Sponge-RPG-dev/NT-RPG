package cz.neumimto.rpg.events.entity;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.effect.TargetIEffectConsumer;

/**
 * Base event for when a {@link IEntity} is a target.
 */
public interface TargetIEntityEvent extends TargetIEffectConsumer {
	@Override
	IEntity getTarget();
}
