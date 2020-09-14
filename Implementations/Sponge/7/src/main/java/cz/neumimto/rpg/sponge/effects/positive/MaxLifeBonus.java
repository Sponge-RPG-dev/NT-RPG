package cz.neumimto.rpg.sponge.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.entity.IEffectConsumer;


public class MaxLifeBonus extends EffectBase {

    public static final String name = "Max life";

    public MaxLifeBonus(String name, IEffectConsumer consumer) {
        super(name, consumer);
    }
}
