package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by ja on 20.8.2017.
 */
@ResourceLoader.Skill
public class Stun extends Targetted {

	@Inject
	private EntityService entityService;


	@Inject
	private EffectService effectService;

	public Stun() {
		setName(SkillLocalization.SKILL_STUN_NAME);
		setDescription(SkillLocalization.SKILL_STUN_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DAMAGE, 10, 1);
		settings.addNode(SkillNodes.DURATION, 4500, 100);
		setSettings(settings);
		addSkillType(SkillType.PHYSICAL);
		addSkillType(SkillType.MOVEMENT);
		setDamageType(DamageTypes.ATTACK);
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		double damage = getDoubleNodeValue(info, SkillNodes.DAMAGE);
		IEntity e = entityService.get(target);
		StunEffect stunEffect = new StunEffect(e, duration);
		effectService.addEffect(stunEffect, e, this);
		if (damage > 0) {
			SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();
			builder.fromSkill(this);
			builder.setTarget(e);
			builder.setCaster(source);
			SkillDamageSource s = builder.build();
			target.damage(damage, s);
		}
		return SkillResult.OK;
	}

}
