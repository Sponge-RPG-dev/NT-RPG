package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.event.Cancellable;

public abstract class AbstractCharacterCancellableEvent extends AbstractCharacterEvent implements Cancellable {
	protected boolean cancelled;

	public AbstractCharacterCancellableEvent(IActiveCharacter character) {
		super(character);
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
