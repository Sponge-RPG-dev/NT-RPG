

package cz.neumimto.rpg.sponge.effects.positive;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by NeumimTo on 23.7.2015.
 */

/**
 * An example class how to use Classgenerator.
 * <p>
 * The annotation will generate according global effect class at runtime.
 * id - field name of unique identifier (in most cases its name), the field must be static and public
 * inject - If set to true the class loader tries to inject public static field which is assingable from IGlobalEffect.
 * Main behavior of global effect is that they are accessible via effectservice.getGlobalEffect(stringId) inject option is
 * here only if someone would like to keep direct field reference to the global effect object.
 * Global Effects may be given to player via command or as an item enchantement
 * <p>
 * The class, which inherits from IEffect(or its implementations such as effect base) must contain a constructor - IEffectConsumer, long duration,
 * int level.
 * <p>
 * Global effect can work as item enchantments, and be accessible from commands
 */
@Generate(id = "name", inject = true, description = "An effect which increases target walk speed")
public class SpeedBoost extends EffectBase {

    public static final String name = "Speed";

    public static IGlobalEffect<SpeedBoost> global;

    private double speedbonus;

    public SpeedBoost(IEffectConsumer consumer, long duration, double speedbonus) {
        super(name, consumer);
        this.speedbonus = speedbonus;
        setDuration(duration);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onApply(IEffect self) {
        super.onApply(self);
        getConsumer().setProperty(CommonProperties.walk_speed,
                getConsumer().getProperty(CommonProperties.walk_speed) + (float) speedbonus);
        ISpongeEntity consumer = (ISpongeEntity) getConsumer();
        Rpg.get().getEntityService().updateWalkSpeed(consumer);
        Location<World> location = consumer.getLocation();

        ParticleEffect build = ParticleEffect.builder()
                .type(ParticleTypes.CLOUD)
                .velocity(new Vector3d(0, 0.8, 0))
                .quantity(2).build();
        for (Vector3d vector3d : ParticleDecorator.smallCircle) {
            location.getExtent().spawnParticles(build, location.getPosition().add(vector3d));
        }
    }

    @Override
    public void onRemove(IEffect self) {
        super.onRemove(self);
        getConsumer().setProperty(CommonProperties.walk_speed,
                Rpg.get().getEntityService().getEntityProperty(getConsumer(), CommonProperties.walk_speed) - (float) speedbonus);
        Rpg.get().getEntityService().updateWalkSpeed((ISpongeEntity) getConsumer());
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

}
