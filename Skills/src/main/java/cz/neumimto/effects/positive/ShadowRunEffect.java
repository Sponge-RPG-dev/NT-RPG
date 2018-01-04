package cz.neumimto.effects.positive;

import cz.neumimto.model.ShadowRunModel;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.utils.XORShiftRnd;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@ClassGenerator.Generate(id = "name")
public class ShadowRunEffect extends EffectBase<ShadowRunModel> {

    public static final String name = "ShadowRun";
    public static XORShiftRnd rnd = new XORShiftRnd();

    public ShadowRunEffect(IEffectConsumer character, ShadowRunModel shadowRunModel) {
        super(name, character);
        setStackable(false, null);
        setValue(shadowRunModel);
        setDuration(shadowRunModel.duration);
        setPeriod(20);
    }

    public ShadowRunEffect(IEffectConsumer consumer, long duration, String value) {
        this(consumer, duration, ShadowRunModel.parse(value));
    }

    @Override
    public void onApply() {
        super.onApply();
        getConsumer().getEntity().offer(Keys.VANISH, true);
        getConsumer().getEntity().offer(Keys.VANISH_PREVENTS_TARGETING, true);
        getConsumer().addProperty(DefaultProperties.walk_speed, getValue().walkspeed);
        NtRpgPlugin.GlobalScope.characterService.updateWalkSpeed(getConsumer());
    }

    @Override
    public void onTick() {
        int i = rnd.nextInt(5);
        Location<World> location = getConsumer().getLocation();
        World extent = location.getExtent();
        extent.spawnParticles(ParticleEffect.builder()
                .quantity(i)
                .type(ParticleTypes.SMOKE)
                .build(),
                location.getPosition().add(0,1,0),
                5);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        getConsumer().getEntity().offer(Keys.VANISH, false);
        getConsumer().getEntity().offer(Keys.VANISH_PREVENTS_TARGETING, false);
        getConsumer().addProperty(DefaultProperties.walk_speed, -getValue().walkspeed);
        NtRpgPlugin.GlobalScope.characterService.updateWalkSpeed(getConsumer());
    }
}
