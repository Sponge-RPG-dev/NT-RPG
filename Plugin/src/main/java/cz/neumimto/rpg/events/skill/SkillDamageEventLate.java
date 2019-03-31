package cz.neumimto.rpg.events.skill;

import cz.neumimto.rpg.damage.ISkillDamageSource;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.entity.DamageIEntityEvent;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.event.cause.Cause;

/**
 * Called when IEntity gets damaged by other IEntity by skill or effect
 * Fired after bonuses of source calculation and after resistances of target
 * {@link Cause} contains {@link ISkillDamageSource}
 */
@JsBinding(JsBinding.Type.CLASS)
public class SkillDamageEventLate extends DamageIEntityEvent implements SkillEvent {
	private final ISkill skill;
	private double targetResistance;

	public SkillDamageEventLate(IEntity target, ISkill skill, double damage, double resistance) {
		super(target, damage);
		this.skill = skill;
		this.targetResistance = resistance;
	}

	@Override
	public ISkill getSkill() {
		return skill;
	}

	public double getTargetResistance() {
		return targetResistance;
	}

	public void setTargetResistance(double targetResistance) {
		this.targetResistance = targetResistance;
	}

}
