package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@ResourceLoader.Skill("ntrpg:wrestle")
public class Wrestle extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Inject
	private EntityService entityService;

	public void init() {
		super.init();
		setDamageType(NDamageType.PHYSICAL);
		settings.addNode(SkillNodes.RADIUS, 3, 0.5f);
		settings.addNode(SkillNodes.DURATION, 1, 0.1f);
		settings.addNode(SkillNodes.DAMAGE, 1, 0.5f);
		addSkillType(SkillType.PHYSICAL);
		addSkillType(SkillType.AOE);
	}

	@Override
	public void cast(IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		int intNodeValue = skillContext.getIntNodeValue(SkillNodes.RADIUS);
		float floatNodeValue = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		for (Entity entity : source.getPlayer().getNearbyEntities(intNodeValue)) {
			if (Utils.isLivingEntity(entity)) {
				Living l = (Living) entity;
				if (Utils.canDamage(source, l)) {
					IEffectConsumer t = entityService.get(l);
					StunEffect stunEffect = new StunEffect(t, duration);
					effectService.addEffect(stunEffect, this);
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
		skillContext.next(source, info, skillContext.result(SkillResult.OK));
	}
}
