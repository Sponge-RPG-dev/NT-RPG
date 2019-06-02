package cz.neumimto.effects.global;

import cz.neumimto.effects.positive.PotionEffect;
import cz.neumimto.model.PotionEffectModel;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Map;
import java.util.Optional;

public class PotionEffectGlobal implements IGlobalEffect<PotionEffect> {

    @Override
    public PotionEffect construct(IEffectConsumer consumer, long duration, Map<String, String> data) {
        PotionEffectModel model = new PotionEffectModel();
        for (Map.Entry<String, String> stringEntry : data.entrySet()) {
            Optional<PotionEffectType> type = Sponge.getRegistry().getType(PotionEffectType.class, stringEntry.getKey());
            if (type.isPresent()) {
                model.cooldowns.put(type.get(), Long.parseLong(stringEntry.getValue()));
            } else {
                Log.warn(" Unknown potion type " + stringEntry.getKey()+"!");
            }
        }
        return new PotionEffect(consumer, duration, model);
    }

    @Override
    public String getName() {
        return PotionEffect.name;
    }

    @Override
    public Class<PotionEffect> asEffectClass() {
        return PotionEffect.class;
    }
}
