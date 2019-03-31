package cz.neumimto.rpg.events.entity;

import cz.neumimto.rpg.entities.IEntity;
import org.spongepowered.api.event.Cancellable;

public abstract class AbstractIEntityCancellableEvent extends AbstractIEntityEvent implements Cancellable {
	protected boolean cancelled;

	public AbstractIEntityCancellableEvent(IEntity target) {
		super(target);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
