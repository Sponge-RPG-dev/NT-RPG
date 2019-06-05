package cz.neumimto.events;

import cz.neumimto.rpg.api.events.Cancellable;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.sponge.events.AbstractCancellableNEvent;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import org.spongepowered.api.event.Event;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class CriticalStrikeEvent implements Cancellable, Event {

	private final IEntity source;
	private final IEntity target;
	private final double damage;

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
}
