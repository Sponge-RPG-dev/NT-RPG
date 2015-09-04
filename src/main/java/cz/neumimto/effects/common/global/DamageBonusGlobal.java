package cz.neumimto.effects.common.global;

import cz.neumimto.effects.IEffectConsumer;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.effects.common.positive.DamageBonus;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public class DamageBonusGlobal implements IGlobalEffect<DamageBonus> {
    public DamageBonusGlobal() {
    }

    @Override
    public DamageBonus construct(IEffectConsumer consumer, long duration, int level) {
        return new DamageBonus(consumer, duration, level);
    }

    @Override
    public String getName() {
        return DamageBonus.name;
    }
}
