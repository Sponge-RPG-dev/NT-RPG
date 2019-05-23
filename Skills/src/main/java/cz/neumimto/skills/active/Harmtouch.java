package cz.neumimto.skills.active;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targeted;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:harmtouch")
public class Harmtouch extends Targeted {

	@Inject
	private EntityService entityService;

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.DAMAGE, 5000, 100);
		setDamageType(DamageTypes.MAGIC);
		setIcon(ItemTypes.BLAZE_ROD);
	}

	@Override
	public void castOn(IEntity target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		SkillDamageSource s = new SkillDamageSourceBuilder()
				.fromSkill(this)
				.setSource(source)
				.build();
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		boolean damage1 = target.getEntity().damage(damage, s);
		if (damage1) {
			Vector3d r = source.getEntity().getRotation();
			Vector3d dir = Quaterniond.fromAxesAnglesDeg(r.getX(), -r.getY(), r.getZ()).getDirection();
			Location<World> location = target.getEntity().getLocation();
			location.getExtent().spawnParticles(ParticleEffect.builder()
							.option(ParticleOptions.COLOR, Color.ofRgb(207, 23, 255))
							.option(ParticleOptions.QUANTITY, 3)
							.velocity(dir.normalize())
							.build(),
					target.getEntity().getLocation().getPosition()
			);

			location.getExtent().spawnParticles(ParticleEffect.builder()
							.option(ParticleOptions.COLOR, Color.RED)
							.option(ParticleOptions.QUANTITY, 5)
							.velocity(dir.normalize().mul(1.5))
							.build(),
					target.getEntity().getLocation().getPosition());
		}
		skillContext.next(source, info, SkillResult.OK);
	}
}
