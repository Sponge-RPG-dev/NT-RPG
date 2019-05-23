package cz.neumimto.rpg.events.damage;

import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.damage.ISkillDamageSource;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.skill.SkillEvent;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when IEntity gets damaged by skill or effect, after damage bonuses of source, but before resistances of target are applied
 * {@link Cause} contains {@link ISkillDamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class IEntitySkillDamageEarlyEvent extends DamageIEntityEarlyEvent implements SkillEvent {
	private final ISkill skill;

	public IEntitySkillDamageEarlyEvent(IEntity target, ISkill skill, double damage) {
		super(target, damage);
		this.skill = skill;
	}

	@Override
	public ISkill getSkill() {
		return skill;
	}
}
