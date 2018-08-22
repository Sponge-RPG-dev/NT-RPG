package cz.neumimto.effects.positive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.decoration.ParticleSpawner;
import cz.neumimto.model.ManaShieldEffectModel;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.gui.ParticleDecorator;
import cz.neumimto.rpg.scripting.JsBinding;
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

    public ManaShieldEffect(IEffectConsumer consumer, @Inject ManaShieldEffectModel model) {
        super(name, consumer);
        setDuration(model.duration);
        setValue(model);
        setStackable(false, null);
    }

    @Override
    public void onApply() {
        super.onApply();
        init(apply_effect, getConsumer().getEntity());
    }

    @Override
    public void onRemove() {
        super.onRemove();
        remove(remove_effect, getConsumer().getEntity());
    }

    public void init(ParticleEffect pe, Entity e) {
        Sponge.getScheduler().createTaskBuilder()
                .execute(new ParticleSpawner(pe, e, 0.3d, 1d, 4, 0, ParticleDecorator.tinyCircle))
                .interval(2, TimeUnit.MILLISECONDS)
                .submit(NtRpgPlugin.GlobalScope.plugin);
    }

    public void remove(ParticleEffect pe, Entity e) {
        Sponge.getScheduler().createTaskBuilder()
                .execute(new ParticleSpawner(pe, e, -0.3d, -1d, 4, 2, ParticleDecorator.tinyCircle))
                .interval(2, TimeUnit.MILLISECONDS)
                .submit(NtRpgPlugin.GlobalScope.plugin);
    }
}
