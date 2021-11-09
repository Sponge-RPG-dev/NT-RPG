package cz.neumimto.rpg.spigot.events.effects;

import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.events.effect.EffectRemoveEvent;
import org.bukkit.event.HandlerList;

public class SpigotEffectRemoveEvent<T extends IEffect> extends AbstractEffectEvent<T> implements EffectRemoveEvent<T> {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
