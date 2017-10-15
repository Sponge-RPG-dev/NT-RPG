package cz.neumimto.skills.active;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.TippedArrow;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.flowpowered.math.TrigMath.cos;
import static com.flowpowered.math.TrigMath.sin;

/**
 * Created by NeumimTo on 5.8.2017.
 */
@ResourceLoader.Skill
public class GrapplingHook extends ActiveSkill {

	public static Map<UUID, Long> cache = new LinkedHashMap<UUID, Long>() {
		@Override
		protected boolean removeEldestEntry(Map.Entry<UUID, Long> eldest) {
			return eldest.getValue() + 15000L < System.currentTimeMillis();
		}
	};

	@Inject
	private NtRpgPlugin plugin;

	public GrapplingHook() {
		setName(SkillLocalization.GRAPPLING_HOOK_NAME);
		setDescription(SkillLocalization.GRAPPLING_HOOK_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.RANGE, 100, 10);
		setSettings(settings);
		addSkillType(SkillType.PHYSICAL);
		addSkillType(SkillType.PROJECTILE);
		addSkillType(SkillType.SUMMON);
		addSkillType(SkillType.STEALTH);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		Player p = character.getPlayer();
		World world = p.getWorld();
		Entity optional = world.createEntity(EntityTypes.TIPPED_ARROW, p.getLocation().getPosition().add(cos((p.getRotation().getX() - 90) % 360) * 0.2, 1.8, sin((p.getRotation().getX() - 90) % 360) * 0.2));

		Vector3d rotation = p.getRotation();
		Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
		TippedArrow sb = (TippedArrow) optional;
		sb.setShooter(p);


		Vector3d arrowVec = direction.normalize().mul(2);
		sb.setVelocity(arrowVec);
		double range = getDoubleNodeValue(info, SkillNodes.RANGE);
		//final double rangeSquared = Math.pow(range, 2);

		world.spawnEntity(sb);
		cache.put(sb.getUniqueId(), System.currentTimeMillis());
		ParticleEffect eff = ParticleEffect.builder()
				.quantity(1)
				.type(ParticleTypes.CRITICAL_HIT)
				.build();

		Sponge.getScheduler().createTaskBuilder().execute(new Consumer<Task>() {

			double distTraveledSquared = 0;
			Location<World> prev;

			@Override
			public void accept(Task task) {
				if (sb.isRemoved()) {
					task.cancel();
					return;
				}
				if (prev != null) {
					distTraveledSquared += sb.getLocation().getPosition().distance(prev.getPosition());
				}

				if (distTraveledSquared >= range) {
					cache.remove(sb.getUniqueId());
					sb.remove();
					task.cancel();
					return;
				}
				prev = sb.getLocation();
				sb.setVelocity(arrowVec);
				double dist = p.getLocation().getPosition().distance(sb.getLocation().getPosition());
				Location<World> to = p.getLocation().copy();
				double printer = 0.0D;
				while (printer < dist) {
					to.getExtent().spawnParticles(eff, to.getPosition());
					to = to.add(sb.getLocation().getPosition().sub(p.getLocation().getPosition()).normalize());
					printer = to.getPosition().distance(p.getLocation().getPosition());
				}
			}
		}).delay(0, TimeUnit.MILLISECONDS)
				.interval(50, TimeUnit.MILLISECONDS)
				.submit(plugin);

		return SkillResult.OK;
	}


}
