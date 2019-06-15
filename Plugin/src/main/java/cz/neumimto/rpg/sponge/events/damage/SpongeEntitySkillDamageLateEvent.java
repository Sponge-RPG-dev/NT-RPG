package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.IEntitySkillDamageLateEvent;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.sponge.damage.ISkillDamageSource;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when IEntity gets damaged by skill or effect, after damage bonuses of source and resistances of target are applied
 * {@link Cause} contains {@link ISkillDamageSource}
 */
public class SpongeEntitySkillDamageLateEvent extends SpongeAbstractDamageEvent implements IEntitySkillDamageLateEvent, Cancellable {

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
}
