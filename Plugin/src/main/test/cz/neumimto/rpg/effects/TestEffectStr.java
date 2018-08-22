package cz.neumimto.rpg.effects;

/**
 * Created by NeumimTo on 25.2.2018.
 */
@Generate(id = "name",description = "test")
public class TestEffectStr extends EffectBase<String> {
    public static String name = "Test";

    public TestEffectStr(IEffectConsumer character, long duration, String testModel) {
        super(name, character);
        setDuration(duration);
        setValue(testModel);
    }

}