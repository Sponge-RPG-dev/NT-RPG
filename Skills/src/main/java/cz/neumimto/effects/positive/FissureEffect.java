package cz.neumimto.effects.positive;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Builds a tempoar wall of dirt")
public class FissureEffect extends EffectBase<Integer> {

    public static Set<UUID> animatedBlocks = new HashSet<>();
    public static String name = "Fissure";

    private Vector3d direction;
    private Vector3d position;
    private Iterator<BlockRayHit<World>> iterator;

    private static Vector3d velocity1 = new Vector3d(0,-2,0);
    private static Vector3d velocity2 = new Vector3d(0,2,0);

    public FissureEffect(IEffectConsumer character, long duration, int range) {
        super(name, character);
        setValue(range);
        setStackable(false, null);
        setDuration(duration);
        setPeriod(125);

        Living entity = character.getEntity();
        Vector3d r = entity.getRotation();
        direction = Quaterniond.fromAxesAnglesDeg(r.getX(), -r.getY(), r.getZ()).getDirection();
        direction = new Vector3d(direction.getX(), 0, direction.getZ());
        Optional<EyeLocationProperty> data = entity.getProperty(EyeLocationProperty.class);
        position = data.map(EyeLocationProperty::getValue).orElse(entity.getLocation().getPosition()).add(0,-1,0);

        iterator = BlockRay.from(entity.getLocation().getExtent(), position)
                .narrowPhase(false)
                .distanceLimit(range)
                .direction(direction)
                .iterator();

    }

    @Override
    public void onTick(IEffect self) {
        if (iterator.hasNext()) {
            BlockRayHit<World> next = iterator.next();
            Location<World> location = next.getLocation();
            World world = location.getExtent();

            Entity block = world.createEntity(EntityTypes.FALLING_BLOCK, next.getBlockPosition());
            block.offer(Keys.VELOCITY, velocity1);
            block.offer(Keys.FALLING_BLOCK_STATE, world.getBlock(next.getBlockPosition().add(0,-1,0)));
            world.spawnEntity(block);
        }
    }

    @Override
    public void onRemove(IEffect self) {
        super.onRemove(self);
    }
}
