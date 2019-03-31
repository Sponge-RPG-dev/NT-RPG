package cz.neumimto.rpg.events.skill;

import cz.neumimto.rpg.damage.ISkillDamageSource;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.entity.DamageIEntityEvent;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when IEntity gets damaged by other IEntity by skill or effect
 * Fired after bonuses of source calculation, but before resistances of target
 * {@link Cause} contains {@link ISkillDamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class SkillDamageEvent extends DamageIEntityEvent implements SkillEvent {
	private final ISkill skill;

	public SkillDamageEvent(IEntity target, ISkill skill, double damage) {
		super(target, damage);
		this.skill = skill;
	}

	@Override
	public ISkill getSkill() {
		return skill;
	}
}
