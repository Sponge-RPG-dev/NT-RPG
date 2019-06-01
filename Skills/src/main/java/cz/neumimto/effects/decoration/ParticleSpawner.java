package cz.neumimto.effects.decoration;


import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

public class ParticleSpawner implements Consumer<Task> {

	private final ParticleEffect pe;
	private final Entity e;
	private final double layerSpace;
	private final double i;
	private final Vector3d[] template;
	private int max;
	private int currentStep = 0;
	private double currentY = 0;

	public ParticleSpawner(ParticleEffect pe, Entity e, double layerSpace, double i, int max, double startY, Vector3d[] template) {
		this.pe = pe;
		this.e = e;
		this.layerSpace = layerSpace;
		this.i = i;
		this.max = max;
		this.template = template;
		currentY = startY;
	}

	@Override
	public void accept(Task task) {
		if (currentStep > max) {
			task.cancel();
		}
		World extent = e.getLocation().getExtent();
		Vector3d position = e.getLocation().getPosition();
		for (Vector3d vector3d : template) {
			extent.spawnParticles(pe, vector3d.add(position.getX(), position.getY() + currentY, position.getZ()));
		}
		currentStep++;
		currentY = currentStep * layerSpace * i;
	}
}
