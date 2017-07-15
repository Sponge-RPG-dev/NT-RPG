package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.event.cause.entity.damage.DamageType;

/**
 * Created by NeumimTo on 18.6.2017.
 */
public class SkillDamageEvent extends INEntityDamageEvent {
	ISkill skill;

	public SkillDamageEvent(IEntity caster, IEntity target, ISkill skill, double damage, DamageType damageType) {
		super(caster, target, damage, damageType);
		this.skill = skill;
	}

	public ISkill getSkill() {
		return skill;
	}

	public void setSkill(ISkill skill) {
		this.skill = skill;
	}
}
