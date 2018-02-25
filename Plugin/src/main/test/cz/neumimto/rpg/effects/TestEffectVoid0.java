package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.ClassGenerator;

/**
 * Created by NeumimTo on 25.2.2018.
 */
@ClassGenerator.Generate(id = "name")
public class TestEffectVoid0 extends EffectBase<Void> {
    public static String name = "Test";

    public TestEffectVoid0(IEffectConsumer character, long duration, Void testModel) {
        super(name, character);
        setDuration(duration);
        setValue(testModel);
    }

}