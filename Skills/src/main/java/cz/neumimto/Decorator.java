package cz.neumimto;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 6.2.2016.
 */

public class Decorator {

	public static ParticleDecorator decorator;
	public static ParticleEffect healingEffect = ParticleEffect.builder()
			.quantity(3)
			.type(ParticleTypes.HEART)
			.offset(new Vector3d(1, 0, 1))
			.velocity(new Vector3d(0, 1, 0).normalize())
			.build();

	static {
		decorator = SpongeRpgPlugin.getInstance().injector.getInstance(ParticleDecorator.class);
	}

	public static void strikeLightning(Entity entity) {
		strikeLightning(entity.getLocation());
	}

	public static void strikeLightning(Location<World> location) {
		decorator.strikeLightning(location);
	}

	public static void createTrajectory(Entity entity, int interval, int maxticks, BiConsumer<Task, Entity> e) {
		decorator.createTrajectory(entity, interval, maxticks, e);
	}

	public static void circle(Location location, int count, double radius, Consumer<Location> callback) {
		decorator.circle(location, count, radius, callback);
	}

	public static void ellipse(Vector3d[] vector3ds, double a, double b, double vecmult, Vector3d rotationAngle) {
		decorator.ellipse(vector3ds, a, b, vecmult, rotationAngle);
	}

	public static void healEffect(Location<World> worldLocation) {
		worldLocation.getExtent().spawnParticles(healingEffect, worldLocation.getPosition());
	}
}
