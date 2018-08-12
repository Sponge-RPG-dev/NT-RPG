package cz.neumimto.effects.positive;

import static com.flowpowered.math.TrigMath.cos;
import static com.flowpowered.math.TrigMath.sin;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.Set;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@ClassGenerator.Generate(id = "name", description = "A periodic effect which shoots an arrow in a way the entity is looking at every x ticks")
public class ArrowstormEffect extends EffectBase implements IEffectContainer {

	public static final String name = "Arrowstorm";
	private int arrows;

	public ArrowstormEffect(IEffectConsumer consumer, long period, int arrows) {
		super(name, consumer);
		this.arrows = arrows;
		setDuration(-1L);
		setPeriod(period);
	}

	@Override
	public void onTick() {
		if (arrows != 0) {
			Living entity = getConsumer().getEntity();
			World world = entity.getWorld();
			Vector3d rotation = entity.getRotation();
			Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();

			Entity arrow = world.createEntity(EntityTypes.TIPPED_ARROW,
					entity.getLocation().getPosition()
							.add(cos((entity.getRotation().getX() - 90) % 360) * 0.2,
									1.8,
									sin((entity.getRotation().getX() - 90) % 360) * 0.2));
			Arrow sb = (Arrow) arrow;
			sb.setShooter(entity);
			world.spawnEntity(sb);
			sb.offer(Keys.VELOCITY, direction.mul(3f));
			arrows--;
		} else {
			setDuration(0); //remove the effect next effect scheduler phase
		}
	}

	@Override
	public Set<ArrowstormEffect> getEffects() {
		return Collections.singleton(this);
	}

	@Override
	public Object getStackedValue() {
		return null;
	}

	@Override
	public void removeStack(IEffect iEffect) {

	}

	@Override
	public void setStackedValue(Object o) {

	}
}
