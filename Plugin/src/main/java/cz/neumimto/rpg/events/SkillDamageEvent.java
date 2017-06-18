package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.event.cause.entity.damage.DamageType;

/**
 * Created by NeumimTo on 18.6.2017.
 */
public class SkillDamageEvent extends CancellableEvent {
	IEntity caster;
	IEntity target;
	ISkill skill;
	double damage;
	DamageType damageType;

	public SkillDamageEvent(IEntity caster, IEntity target, ISkill skill, double damage, DamageType damageType) {
		this.caster = caster;
		this.target = target;
		this.skill = skill;
		this.damage = damage;
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
}
