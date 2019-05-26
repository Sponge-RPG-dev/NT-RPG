package cz.neumimto.rpg.api.events.damage;

import cz.neumimto.rpg.sponge.damage.ISkillDamageSource;
import cz.neumimto.rpg.api.events.skill.SkillEvent;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when IEntity gets damaged by skill or effect, after damage bonuses of source and resistances of target are applied
 * {@link Cause} contains {@link ISkillDamageSource}
 */
public interface IEntitySkillDamageLateEvent extends DamageIEntityEarlyEvent, SkillEvent {

}
