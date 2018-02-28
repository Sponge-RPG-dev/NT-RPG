package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.ClassGenerator;

/**
 * Created by NeumimTo on 25.2.2018.
 */
@ClassGenerator.Generate(id = "name")
public class TestEffectStr extends EffectBase<String> {
    public static String name = "Test";

    public TestEffectStr(IEffectConsumer character, long duration, String testModel) {
        super(name, character);
        setDuration(duration);
        setValue(testModel);
    }

}