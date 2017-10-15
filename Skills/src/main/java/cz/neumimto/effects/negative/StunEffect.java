package cz.neumimto.effects.negative;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.CommonEffectTypes;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.ShapedEffectDecorator;
import cz.neumimto.rpg.gui.ParticleDecorator;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

/**
 * Created by NeumimTo on 5.6.17.
 */
@ClassGenerator.Generate(id = "name")
public class StunEffect extends ShapedEffectDecorator<Location<World>> {

	public static final String name = "Stun";

	private static ParticleEffect particleEffect = ParticleEffect.builder()
			.quantity(8)
			.type(ParticleTypes.CRITICAL_HIT)
			.build();

	public StunEffect(IEffectConsumer consumer, long duration, String value) {
		this(consumer, duration);
	}

	public StunEffect(IEffectConsumer consumer, long duration) {
		super(name, consumer);
		setValue(consumer.getEntity().getLocation());
		setDuration(duration);
		setPeriod(10);
		setPrinterCount(1);
		addEffectType(CommonEffectTypes.SILENCE);
		addEffectType(CommonEffectTypes.STUN);
	}


	@Override
	public void onTick() {
		super.onTick();
		if (getLastTickTime() <= System.currentTimeMillis() - 50L) {
			getConsumer().getEntity().setLocation(getValue());
		}
	}

	@Override
	public void draw(Vector3d vec) {
		Location<Extent> add = getConsumer().getLocation().add(vec).add(0, 2, 0);
		World extent = getConsumer().getEntity().getLocation().getExtent();
		extent.spawnParticles(particleEffect, add.getPosition());

	}

	@Override
	public Vector3d[] getVertices() {
		return ParticleDecorator.tinyCircle;
	}

}
