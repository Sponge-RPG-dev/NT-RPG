package cz.neumimto.rpg.sponge.effects.positive;


import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.effects.decoration.ParticleSpawner;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.model.ManaShieldEffectModel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Color;

import java.util.concurrent.TimeUnit;

@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Redirects all incoming damage to mana")
public class ManaShieldEffect extends EffectBase<ManaShieldEffectModel> {

    public static final String name = "ManaShield";

    public static ParticleEffect apply_effect = ParticleEffect.builder()
            .quantity(3)
            .type(ParticleTypes.REDSTONE_DUST)
            .option(ParticleOptions.COLOR, Color.BLUE)
            .build();

    public static ParticleEffect remove_effect = ParticleEffect.builder()
            .quantity(3)
            .type(ParticleTypes.REDSTONE_DUST)
            .option(ParticleOptions.COLOR, Color.GRAY)
            .build();

    public ManaShieldEffect(IEffectConsumer consumer, long duration, ManaShieldEffectModel model) {
        super(name, consumer);
        setDuration(duration);
        setValue(model);
        setStackable(false, null);
    }

    @Override
    public void onApply(IEffect self) {
        super.onApply(self);
        init(apply_effect, ((ISpongeEntity) getConsumer()).getEntity());
    }

    @Override
    public void onRemove(IEffect self) {
        super.onRemove(self);
        remove(remove_effect, ((ISpongeEntity) getConsumer()).getEntity());
    }

    public void init(ParticleEffect pe, Entity e) {
        Sponge.getScheduler().createTaskBuilder()
                .execute(new ParticleSpawner(pe, e, 0.3d, 1d, 4, 0, ParticleDecorator.tinyCircle))
                .interval(2, TimeUnit.MILLISECONDS)
                .submit(SpongeRpgPlugin.getInstance());
    }

    public void remove(ParticleEffect pe, Entity e) {
        Sponge.getScheduler().createTaskBuilder()
                .execute(new ParticleSpawner(pe, e, -0.3d, -1d, 4, 2, ParticleDecorator.tinyCircle))
                .interval(2, TimeUnit.MILLISECONDS)
                .submit(SpongeRpgPlugin.getInstance());
    }
}
