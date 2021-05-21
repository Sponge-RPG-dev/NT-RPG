

package cz.neumimto.rpg.sponge.effects.global;

import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.effects.positive.DamageBonus;

import java.util.Map;

/**
 * Created by NeumimTo on 6.8.2015.
 */

/**
 * An example class how to manually create global effect
 */
public class DamageBonusGlobal implements IGlobalEffect<DamageBonus> {

    public DamageBonusGlobal() {
    }

    @Override
    public DamageBonus construct(IEffectConsumer consumer, long duration, Map<String, String> value) {
        return new DamageBonus(consumer, duration, EffectModelFactory.create(DamageBonus.class, value, Float.class));
    }

    @Override
    public String getName() {
        return DamageBonus.name;
    }

    @Override
    public Class<DamageBonus> asEffectClass() {
        return DamageBonus.class;
    }
}
