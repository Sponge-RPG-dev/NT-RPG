package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

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
