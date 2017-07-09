package cz.neumimto.effects.decoration;

import cz.neumimto.Decorator;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.gui.IActionDecorator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Created by NeumimTo on 9.7.2017.
 */
@Singleton
public class ParticleDecorator implements IActionDecorator {

	@Inject
	private NtRpgPlugin plugin;

	@Override
	public void strikeLightning(Location<World> location) {
		Entity q = location.getExtent().createEntity(EntityTypes.LIGHTNING, location.getPosition());
		location.getExtent().spawnEntity(q, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
	}

	@Override
	public void createTrajectory(Entity entity, int interval, int maxticks, BiConsumer<Task, Entity> e) {
		Sponge.getScheduler()
				.createTaskBuilder()
				.delay(1L, TimeUnit.MILLISECONDS)
				.interval(interval, TimeUnit.MILLISECONDS)
				.execute((task -> {
			if (!entity.isRemoved()) {
				e.accept(task, entity);
			}
		})).submit(plugin);
	}

	public static class SIMPLE_TRAJECTORY implements BiConsumer<Task, Entity> {

		private ParticleEffect eff;

		public SIMPLE_TRAJECTORY(ParticleEffect eff) {
			this.eff = eff;
		}

		@Override
		public void accept(Task task, Entity entity) {
			entity.getWorld().spawnParticles(eff, entity.getLocation().getPosition());
		}
	}

}
