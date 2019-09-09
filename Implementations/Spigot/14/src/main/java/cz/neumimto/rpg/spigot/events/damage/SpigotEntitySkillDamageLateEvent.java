package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.api.events.damage.IEntitySkillDamageLateEvent;
import cz.neumimto.rpg.api.skills.ISkill;
import org.bukkit.event.HandlerList;


public class SpigotEntitySkillDamageLateEvent extends SpigotAbstractDamageEvent implements IEntitySkillDamageLateEvent {

    private ISkill skill;
    private boolean cancelled;

    @Override
    public ISkill getSkill() {
        return skill;
    }

    @Override
    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
