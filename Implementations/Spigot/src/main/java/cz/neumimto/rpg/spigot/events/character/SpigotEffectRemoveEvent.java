package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.api.effects.IEffect;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpigotEffectRemoveEvent extends Event {
    private final IEffect effect;


    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public SpigotEffectRemoveEvent(IEffect effect) {
        this.effect = effect;
    }

    public IEffect getEffect() {
        return effect;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
