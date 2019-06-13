package cz.neumimto.rpg;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.EffectContainer;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;

public class TickableEffect extends SpongeEffectBase<Long> {

    public static String name = "Test";


    public TickableEffect(String name,IEffectConsumer character, long duration, long model) {
        super(name, character);
        setDuration(duration);
        setPeriod(model);
    }

    @Override
    public IEffectContainer constructEffectContainer() {
        return new EffectContainer<>(this);
    }
}
