package cz.neumimto.rpg.events;

import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

public abstract class AbstractNEvent extends AbstractEvent {
	protected Cause cause = Cause.of(EventContext.empty(), NtRpgPlugin.GlobalScope.plugin);

	@Override
	public Cause getCause() {
		return cause;
	}

	public void setCause(Cause cause) {
		this.cause = cause;
	}

}
