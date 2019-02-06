package cz.neumimto.skills.active;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.Decorator;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.Blindness;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Color;

import java.util.Set;

/**
 * Created by NeumimTo on 15.7.2017.
 */
@ResourceLoader.Skill("ntrpg:despair")
public class Despair extends ActiveSkill {

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		setDamageType(DamageTypes.MAGIC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 1000L, 500);
		settings.addNode(SkillNodes.DAMAGE, 10L, 1.5f);
		settings.addNode(SkillNodes.RADIUS, 7L, 2);
		super.settings = settings;
		addSkillType(SkillType.AOE);
		addSkillType(SkillType.ESCAPE);
		addSkillType(SkillType.STEALTH);
		setIcon(ItemTypes.COAL.getType());
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		int k = skillContext.getIntNodeValue(SkillNodes.RADIUS);
		Set<Entity> nearbyEntities = Utils.getNearbyEntities(character.getEntity().getLocation(), k);
		double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);

		for (Entity nearbyEntity : nearbyEntities) {
			if (Utils.isLivingEntity(nearbyEntity)) {
				Living l = (Living) nearbyEntity;
				if (Utils.canDamage(character, l)) {
					IEntity iEntity = entityService.get(l);
					SkillDamageSource build = new SkillDamageSourceBuilder()
							.fromSkill(this)
							.setTarget(iEntity)
							.setCaster(character).build();
					l.damage(damage, build);
					Blindness blindness = new Blindness(iEntity, duration, null);
					effectService.addEffect(blindness, character, this);
				}
			}
		}

		Vector3d vec = new Vector3d(0, 1, 0);
		Decorator.circle(character.getEntity().getLocation(), 36, k, location -> {
			ParticleEffect build = ParticleEffect.builder()
					.type(ParticleTypes.SPELL)
					.option(ParticleOptions.COLOR, Color.GRAY)
					.build();
			character.getEntity().getLocation().getExtent().spawnParticles(build, location.getPosition().add(vec));
			build = ParticleEffect.builder()
					.type(ParticleTypes.MOB_SPELL)
					.option(ParticleOptions.COLOR, Color.GRAY)
					.build();
			character.getEntity().getLocation().getExtent().spawnParticles(build, location.getPosition().add(vec));
		});

		skillContext.next(character, info, SkillResult.OK);
	}
}
