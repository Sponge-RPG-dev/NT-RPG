package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;

public class FireResistanceEffect extends EffectBase<Long> {

    public FireResistanceEffect(String name, IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setDuration(duration);
        setPeriod(10);
        setStackable(false, null);
    }

    @Override
    public void onApply(IEffect self) {

    }


    @Override
    public void onRemove(IEffect self) {

    }
}
