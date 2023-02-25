package cz.neumimto.rpg.spigot.events.skill;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.skill.SkillTargetAttemptEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class SpigotSkillTargetAttemptEvent extends SpigotAbstractSkillEvent implements SkillTargetAttemptEvent, Cancellable {

    private IEntity target;
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
    public IEntity getTarget() {
        return target;
    }

    @Override
    public void setTarget(IEntity target) {
        this.target = target;
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
