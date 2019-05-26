package cz.neumimto.rpg.sponge.events.damage;

import cz.neumimto.rpg.api.events.damage.IEntitySkillDamageEarlyEvent;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.sponge.damage.ISkillDamageSource;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when IEntity gets damaged by skill or effect, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link ISkillDamageSource}
 */
public class SpongeEntitySkillDamageEarlyEvent extends SpongeAbstractDamageEvent implements IEntitySkillDamageEarlyEvent {

    private ISkill skill;

    @Override
    public ISkill getSkill() {
        return skill;
    }

    @Override
    public void setSkill(ISkill iSkill) {
        this.skill = iSkill;
    }
}
