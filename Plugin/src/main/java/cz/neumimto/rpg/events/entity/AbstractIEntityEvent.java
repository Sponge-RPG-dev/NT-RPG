package cz.neumimto.rpg.events.entity;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.AbstractNEvent;

public abstract class AbstractIEntityEvent extends AbstractNEvent implements TargetIEntityEvent {
	protected final IEntity target;

	public AbstractIEntityEvent(IEntity target) {
		this.target = target;
	}

	@Override
	public IEntity getTarget() {
		return target;
	}
}
