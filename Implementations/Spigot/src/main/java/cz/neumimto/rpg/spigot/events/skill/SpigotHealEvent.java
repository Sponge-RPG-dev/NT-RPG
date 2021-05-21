

package cz.neumimto.rpg.spigot.events.skill;

import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillHealEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public class SpigotHealEvent extends SpigotAbstractSkillEvent implements SkillHealEvent, Cancellable {

    private double amount;
    private IRpgElement source;
    private IEntity entity;
    private boolean cancelled;

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public IRpgElement getSource() {
        return source;
    }

    @Override
    public void setSource(IRpgElement source) {
        this.source = source;
    }

    @Override
    public IEntity getEntity() {
        return entity;
    }

    @Override
    public void setEntity(IEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
