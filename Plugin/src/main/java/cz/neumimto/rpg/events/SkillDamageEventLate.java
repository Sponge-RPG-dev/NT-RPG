package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.event.cause.entity.damage.DamageType;

/**
 * Created by ja on 18.6.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class SkillDamageEventLate extends CancellableEvent {
	IEntity caster;
	IEntity target;
	ISkill skill;
	double damage;
	double targetResistance;
	DamageType damageType;

	public SkillDamageEventLate(IEntity caster, IEntity target, ISkill skill, double damage, double resistance, DamageType type) {
		this.caster = caster;
		this.target = target;
		this.skill = skill;
		this.damage = damage;
		this.damageType = type;
		this.targetResistance = resistance;
	}

	public IEntity getCaster() {
		return caster;
	}

	public void setCaster(IEntity caster) {
		this.caster = caster;
	}

	public IEntity getTarget() {
		return target;
	}

	public void setTarget(IEntity target) {
		this.target = target;
	}

	public ISkill getSkill() {
		return skill;
	}

	public void setSkill(ISkill skill) {
		this.skill = skill;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getTargetResistance() {
		return targetResistance;
	}

	public void setTargetResistance(double targetResistance) {
		this.targetResistance = targetResistance;
	}

	public DamageType getDamageType() {
		return damageType;
	}

	public void setDamageType(DamageType damageType) {
		this.damageType = damageType;
	}
}
