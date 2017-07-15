package cz.neumimto.effects.negative;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.effects.*;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by NeumimTo on 5.6.17.
 */
@ClassGenerator.Generate(id = "name")
public class StunEffect extends EffectBase<Location<World>> {

    public static final String name = "Stun";

    public StunEffect(IEffectConsumer consumer, long duration, String value) {
        this(consumer, duration);
    }

    public StunEffect(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setValue(consumer.getEntity().getLocation());
        setPeriod(50L);
        setDuration(duration);
        addEffectType(CommonEffectTypes.SILENCE);
        addEffectType(CommonEffectTypes.STUN);
    }

    @Override
    public void onTick() {
        getConsumer().getEntity().setLocation(getValue());
    }

}
