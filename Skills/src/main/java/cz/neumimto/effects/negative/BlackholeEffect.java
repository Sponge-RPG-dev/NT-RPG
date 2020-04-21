package cz.neumimto.effects.negative;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.effects.ShapedEffectDecorator;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

/**
 * Created by NeumimTo on 2.8.2017.
 */
public class BlackholeEffect extends ShapedEffectDecorator<Location<World>> {


	public static final String name = "Blackhole";
	private Location<World> targetLocation;
	private AABB aabb;
	private double r;
	private ISpongeCharacter character;

	@Inject
	private SpongeDamageService spongeDamageService;

	public BlackholeEffect(IEffectConsumer consumer, long duration, long lookupPeriod, double diameter, Location<World> targetLocation) {
		super(name, consumer);
		this.targetLocation = targetLocation;
		setDuration(duration);
		setPeriod(lookupPeriod);
		character = (ISpongeCharacter) consumer;
		this.targetLocation = targetLocation;
		r = diameter / 2;
		aabb = new AABB(targetLocation.getX() - r,
				targetLocation.getY() - r,
				targetLocation.getZ() - r,
				targetLocation.getX() + r,
				targetLocation.getY() + r,
				targetLocation.getZ() + r);
	}

	@Override
	public void onTick(IEffect self) {
		Vector3i chunkPosition = targetLocation.getChunkPosition();
		Optional<Chunk> chunk = targetLocation.getExtent().getChunk(chunkPosition);
		if (chunk.isPresent()) {
			Chunk chunk1 = chunk.get();
			Set<Entity> intersectingEntities = chunk1.getIntersectingEntities(aabb);
			for (Entity intersectingEntity : intersectingEntities) {
				if (Utils.isLivingEntity(intersectingEntity)) {
					if (spongeDamageService.canDamage(character, (Living) intersectingEntities)) {
						changeGravity(intersectingEntity);
					}
				} else if (intersectingEntity.getType() == EntityTypes.ITEM) {
					changeGravity(intersectingEntity);
				}
			}
		} else {
			setDuration(0);
		}
	}

	@Override
	public void draw(Vector3d vec) {

	}

	@Override
	public Vector3d[] getVertices() {
		return new Vector3d[0];
	}

	public void changeGravity(Entity entity) {
		Vector3d sub = targetLocation.getPosition().sub(entity.getLocation().getPosition());
		entity.setVelocity(sub.normalize().mul(2));

	}
}
