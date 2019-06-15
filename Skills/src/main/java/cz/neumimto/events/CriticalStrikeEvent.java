package cz.neumimto.events;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.events.AbstractNEvent;
import org.spongepowered.api.event.Cancellable;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class CriticalStrikeEvent extends AbstractNEvent implements Cancellable {

	private final IEntity source;
	private final IEntity target;
	private final double damage;
	private boolean cancelled;

	public CriticalStrikeEvent(IEntity source, IEntity target, double effect) {
		this.source = source;
		this.target = target;
		this.damage = effect;
	}

	@Override
	public IEntity getSource() {
		return source;
	}

	public IEntity getTarget() {
		return target;
	}

	public double getDamage() {
		return damage;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
