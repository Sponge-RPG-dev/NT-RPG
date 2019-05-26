package cz.neumimto.skills.active;

import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:wrestle")
public class Wrestle extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Inject
	private EntityService entityService;

	@Override
	public void init() {
		super.init();
		setDamageType(DamageTypes.ATTACK);
		settings.addNode(SkillNodes.RADIUS, 3, 0.5f);
		settings.addNode(SkillNodes.DURATION, 1, 0.1f);
		settings.addNode(SkillNodes.DAMAGE, 1, 0.5f);
		addSkillType(SkillType.PHYSICAL);
		addSkillType(SkillType.AOE);
	}

	@Override
	public void cast(IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		int radius = skillContext.getIntNodeValue(SkillNodes.RADIUS);
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		for (Entity entity : source.getPlayer().getNearbyEntities(radius)) {
			if (Utils.isLivingEntity(entity)) {
				Living l = (Living) entity;
				if (Utils.canDamage(source, l)) {
					IEffectConsumer t = entityService.get(l);
					StunEffect stunEffect = new StunEffect(t, duration);
					effectService.addEffect(stunEffect, this, source);
					if (damage > 0) {
						SkillDamageSource s = new SkillDamageSourceBuilder()
								.fromSkill(this)
								.setSource(source)
								.build();
						entity.damage(damage, s);
					}
				}
			}
		}
		skillContext.next(source, info, skillContext.result(SkillResult.OK));
	}
}
