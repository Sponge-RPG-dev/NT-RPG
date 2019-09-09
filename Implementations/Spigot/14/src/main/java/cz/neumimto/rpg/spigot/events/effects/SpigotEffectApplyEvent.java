package cz.neumimto.rpg.spigot.events.effects;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.events.effect.EffectApplyEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;


public class SpigotEffectApplyEvent<T extends IEffect> extends AbstractEffectEvent<T> implements EffectApplyEvent<T>, Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
