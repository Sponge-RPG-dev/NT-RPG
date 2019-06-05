package cz.neumimto.skills.active;

import cz.neumimto.effects.negative.PandemicEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:pandemic")
public class Pandemic extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Inject
	private EntityService entityService;

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.RADIUS, 10, 5);
		settings.addNode(SkillNodes.DURATION, 3000, 500);
		settings.addNode(SkillNodes.DAMAGE, 15, 3);
		settings.addNode(SkillNodes.PERIOD, 1500, -10);
		setDamageType(DamageTypes.MAGIC);
		addSkillType(SkillType.AOE);
		addSkillType(SkillType.DISEASE);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		int radius = skillContext.getIntNodeValue(SkillNodes.RADIUS);
		long period = skillContext.getLongNodeValue(SkillNodes.PERIOD);
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		Set<Entity> nearbyEntities = Utils.getNearbyEntities(character.getLocation(), radius);
		for (Entity entity : nearbyEntities) {
			if (Utils.isLivingEntity(entity)) {
				IEntity iEntity = entityService.get(entity);
				if (Utils.canDamage(character, (Living) entity)) {
					PandemicEffect effect = new PandemicEffect(character, iEntity, damage, duration, period);
					SkillDamageSource s = new SkillDamageSourceBuilder()
							.fromSkill(this)
							.setEffect(effect)
							.setSource(character)
							.build();
					effect.setDamageSource(s);
					effectService.addEffect(effect, this);
				}
			}
		}
		skillContext.next(character, info, SkillResult.OK);
	}
}
