package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpigotEffectApplyEvent extends Event implements Cancellable {


    private final IEffect effect;
    private final IEffectSourceProvider effectSourceProvider;
    private final IEntity entitySource;
    private boolean cancelled;

    public SpigotEffectApplyEvent(IEffect effect, IEffectSourceProvider effectSourceProvider, IEntity entitySource) {
        this.effect = effect;
        this.effectSourceProvider = effectSourceProvider;
        this.entitySource = entitySource;
    }

    public IEffect getEffect() {
        return effect;
    }

    public IEffectSourceProvider getEffectSourceProvider() {
        return effectSourceProvider;
    }

    public IEntity getEntitySource() {
        return entitySource;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        this.cancelled = state;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
