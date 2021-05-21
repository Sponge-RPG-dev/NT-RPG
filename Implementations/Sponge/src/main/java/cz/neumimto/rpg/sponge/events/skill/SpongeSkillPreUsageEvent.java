

package cz.neumimto.rpg.sponge.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillPreUsageEvent;
import org.spongepowered.api.event.Cancellable;

/**
 * Created by NeumimTo on 1.8.2015.
 */
public class SpongeSkillPreUsageEvent extends AbstractSkillEvent implements SkillPreUsageEvent, Cancellable {

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

}
