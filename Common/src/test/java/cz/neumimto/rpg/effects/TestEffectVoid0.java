package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.entity.IEffectConsumer;

/**
 * Created by NeumimTo on 25.2.2018.
 */
@Generate(id = "name", description = "test")
public class TestEffectVoid0 extends EffectBase<Void> {

    public static String name = "Test";

    public TestEffectVoid0(IEffectConsumer character, long duration, Void testModel) {
        super(name, character);
        setDuration(duration);
        setValue(testModel);
    }

}