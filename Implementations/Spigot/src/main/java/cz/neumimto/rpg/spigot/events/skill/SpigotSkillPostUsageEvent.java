package cz.neumimto.rpg.spigot.events.skill;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.skill.SkillPostUsageEvent;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public class SpigotSkillPostUsageEvent extends SpigotAbstractSkillEvent implements SkillPostUsageEvent {

    private IEntity caster;
    private long cooldown;

    @Override
    public IEntity getCaster() {
        return caster;
    }

    @Override
    public void setCaster(IEntity caster) {
        this.caster = caster;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
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