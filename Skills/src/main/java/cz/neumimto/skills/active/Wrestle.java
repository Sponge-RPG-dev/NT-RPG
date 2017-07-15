package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@ResourceLoader.Skill
public class Wrestle extends Targetted {

	@Inject
	private EffectService effectService;

	@Inject
	private EntityService entityService;

	public Wrestle() {
		setName("Wrestle");
		setDescription(SkillLocalization.SKILL_WRESTLE_DESC);
		setLore(SkillLocalization.SKILL_WRESTLE_LORE);
		setDamageType(NDamageType.PHYSICAL);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.RADIUS, 3, 0.5f);
		settings.addNode(SkillNodes.DURATION, 1, 0.1f);
		settings.addNode(SkillNodes.DAMAGE, 1, 0.5f);
		super.settings = settings;
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
		int intNodeValue = getIntNodeValue(info, SkillNodes.RADIUS);
		float floatNodeValue = getFloatNodeValue(info, SkillNodes.DAMAGE);
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		for (Entity entity : target.getNearbyEntities(intNodeValue)) {
			if (Utils.isLivingEntity(entity)) {
				Living l = (Living) entity;
				if (Utils.canDamage(source, l)) {
					IEffectConsumer t = entityService.get(target);
					StunEffect stunEffect = new StunEffect(t, duration);
					effectService.addEffect(stunEffect, t, this);
					if (floatNodeValue > 0) {
						SkillDamageSourceBuilder build = new SkillDamageSourceBuilder();
						build.fromSkill(this);
						build.setCaster(source);
						build.type(DamageTypes.ATTACK);
						entity.damage(floatNodeValue, build.build());
					}
				}
			}
		}
		return SkillResult.OK;
	}
}
