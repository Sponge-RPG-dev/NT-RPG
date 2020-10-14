package cz.neumimto.rpg.spigot.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillFinishedEvent;
import org.bukkit.event.HandlerList;

public class SpigotSkillFinishedEvent extends SpigotAbstractSkillEvent implements SkillFinishedEvent {

    private IEntity caster;

    @Override
    public IEntity getCaster() {
        return caster;
    }

    @Override
    public void setCaster(IEntity caster) {
        this.caster = caster;
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