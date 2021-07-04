package cz.neumimto.rpg.spigot.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillPreUsageEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 1.8.2015.
 */
public class SpigotSkillPreUsageEvent extends SpigotAbstractSkillEvent implements SkillPreUsageEvent, Cancellable {

    private IEntity caster;
    private boolean cancelled;

    @Override
    public IEntity getCaster() {
        return caster;
    }

    @Override
    public void setCaster(IEntity caster) {
        this.caster = caster;
    }

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

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
