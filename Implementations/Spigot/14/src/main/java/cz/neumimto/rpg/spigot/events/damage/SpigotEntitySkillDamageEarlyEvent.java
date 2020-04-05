package cz.neumimto.rpg.spigot.events.damage;

import cz.neumimto.rpg.api.events.damage.IEntitySkillDamageEarlyEvent;
import cz.neumimto.rpg.api.skills.ISkill;
import org.bukkit.event.HandlerList;

public class SpigotEntitySkillDamageEarlyEvent extends SpigotAbstractDamageEvent implements IEntitySkillDamageEarlyEvent {

    private ISkill skill;

    @Override
    public ISkill getSkill() {
        return skill;
    }

    @Override
    public void setSkill(ISkill iSkill) {
        this.skill = iSkill;
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
