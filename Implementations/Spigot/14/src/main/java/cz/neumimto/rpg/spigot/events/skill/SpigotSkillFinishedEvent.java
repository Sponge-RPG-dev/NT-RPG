package cz.neumimto.rpg.spigot.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillFinishedEvent;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SpigotSkillFinishedEvent extends SpigotAbstractSkillEvent implements SkillFinishedEvent {

    private SkillContext skillContext;
    private IEntity caster;

    @Override
    public SkillContext getSkillContext() {
        return skillContext;
    }

    @Override
    public void setSkillContext(SkillContext skillContext) {
        this.skillContext = skillContext;
    }

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