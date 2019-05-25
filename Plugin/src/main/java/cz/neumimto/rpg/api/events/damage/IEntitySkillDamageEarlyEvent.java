package cz.neumimto.rpg.api.events.damage;

import cz.neumimto.rpg.damage.ISkillDamageSource;
import cz.neumimto.rpg.api.events.skill.SkillEvent;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when IEntity gets damaged by skill or effect, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link ISkillDamageSource}
 */
public interface IEntitySkillDamageEarlyEvent extends DamageIEntityEarlyEvent, SkillEvent {

}
