package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.ClassGenerator;

/**
 * Created by NeumimTo on 25.2.2018.
 */
@ClassGenerator.Generate(id = "name")
public class TestEffectFloat extends EffectBase<Float> {
    public static String name = "Test";

    public TestEffectFloat(IEffectConsumer character, long duration, float testModel) {
        super(name, character);
        setDuration(duration);
        setValue(testModel);
    }

}
