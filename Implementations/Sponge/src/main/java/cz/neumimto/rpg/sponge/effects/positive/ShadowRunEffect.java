package cz.neumimto.rpg.sponge.effects.positive;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.model.ShadowRunModel;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An invisibility, the next attack will deal increased damage and break the invisibility")
public class ShadowRunEffect extends EffectBase<ShadowRunModel> {

    public static final String name = "ShadowRun";
    public static XORShiftRnd rnd = new XORShiftRnd();

    public ShadowRunEffect(IEffectConsumer character, long duration, ShadowRunModel shadowRunModel) {
        super(name, character);
        setStackable(false, null);
        setValue(shadowRunModel);
        setDuration(duration);
        setPeriod(20);
    }

    @Override
    public void onApply(IEffect self) {
        super.onApply(self);
        ISpongeEntity consumer = (ISpongeEntity) getConsumer();
        Living l = consumer.getEntity();
        l.offer(Keys.VANISH, true);
        l.offer(Keys.VANISH_PREVENTS_TARGETING, true);
        getConsumer().addProperty(CommonProperties.walk_speed, getValue().walkspeed);
        Rpg.get().getEntityService().updateWalkSpeed(consumer);
    }

    @Override
    public void onTick(IEffect self) {
        int i = rnd.nextInt(5);
        Location<World> location = ((ISpongeEntity) getConsumer()).getLocation();
        World extent = location.getExtent();
        extent.spawnParticles(ParticleEffect.builder()
                        .quantity(i)
                        .type(ParticleTypes.SMOKE)
                        .build(),
                location.getPosition().add(0, 1, 0),
                5);
    }

    @Override
    public void onRemove(IEffect self) {
        super.onRemove(self);
        ISpongeEntity consumer = (ISpongeEntity) getConsumer();
        Living l = consumer.getEntity();
        l.offer(Keys.VANISH, false);
        l.offer(Keys.VANISH_PREVENTS_TARGETING, false);
        getConsumer().addProperty(CommonProperties.walk_speed, -getValue().walkspeed);
        Rpg.get().getEntityService().updateWalkSpeed(consumer);
    }
}
