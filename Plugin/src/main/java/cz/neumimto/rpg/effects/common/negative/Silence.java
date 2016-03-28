package cz.neumimto.rpg.effects.common.negative;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.CommonEffectTypes;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;

/**
 * Created by NeumimTo on 17.3.2016.
 */
@ClassGenerator.Generate(id = "name")
public class Silence extends EffectBase {

    public static String name = "Silence";

    public Silence(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setDuration(duration);
        effectTypes.add(CommonEffectTypes.SILENCE);
    }

    public Silence(IEffectConsumer consumer, long duration, float c) {
        this(consumer,duration);
    }

    @Override
    public String getName() {
        return name;
    }
}
