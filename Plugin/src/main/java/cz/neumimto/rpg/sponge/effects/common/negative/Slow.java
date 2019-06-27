package cz.neumimto.rpg.sponge.effects.common.negative;

import cz.neumimto.rpg.api.effects.CommonEffectTypes;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.effects.common.model.SlowModel;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;

@Generate(id = "name", description = "Decreases movement speed")
public class Slow extends EffectBase<SlowModel> {

    public static String name = "Slow";

    public Slow(IEffectConsumer consumer, long duration, SlowModel slowModel) {
        super(name, consumer);
        setValue(slowModel);
        setDuration(duration);
        setPeriod(750L);
        addEffectType(CommonEffectTypes.SILENCE);
    }

    @Override
    public void onTick(IEffect self) {
        SlowModel value = getValue();
        ISpongeEntity entity = (ISpongeEntity) getConsumer();
        if (value.decreasedJumpHeight) {
            PotionEffect pe = PotionEffect.of(PotionEffectTypes.JUMP_BOOST, -1, 17);
            entity.addPotionEffect(pe);
        }
        int slowLevel = value.slowLevel;
        PotionEffect pe = PotionEffect.of(PotionEffectTypes.SLOWNESS, slowLevel, 17);
        entity.addPotionEffect(pe);
    }

    @Override
    public String getName() {
        return name;
    }
}
