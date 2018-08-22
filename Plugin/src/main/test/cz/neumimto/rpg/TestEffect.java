package cz.neumimto.rpg;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;

@Generate(description = "test", id = "name")
public class TestEffect extends EffectBase<TestModel> {
    public static String name = "Test";

    public TestEffect(IEffectConsumer character, long duration, float testModel) {
        super(name, character);
        setDuration(duration);
       // setValue(testModel);
    }

}
